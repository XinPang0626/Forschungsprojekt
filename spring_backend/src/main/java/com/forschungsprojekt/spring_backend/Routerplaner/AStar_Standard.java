package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class AStar_Standard {
	private Graph graph;
	private double[] f;
	private double[] g;//g: cost of past, f = cost of past + cost of future
	private double[] alpha;
	private int[] parent;
	private int start;
	private int target;
	private String heuristic;
	private int[] landmark;
	private ArrayList<ArrayList<CHFilter>> landmarkDistance;
	private int nrOfLandmark;
	private int[][] nrOfCandidate;
	
	public AStar_Standard(Graph graph, String heuristic, int nrOfLandmark) {
		this.graph = graph;
		this.f = new double[graph.getNodeNr()];
		this.g = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.nrOfLandmark = nrOfLandmark;
		this.nrOfCandidate = new int[nrOfLandmark][graph.getNodeNr()];//initialize with zero's
		this.heuristic = heuristic;
		
		// this.filter = new CHFilter[graph.getNodeNr()];
		// for (int i = 0; i < filter.length; i++) {
		// 	filter[i] = new CHFilter(graph.getNrOFMetrik());
		// }
		if(heuristic.equals("ALT")){
			this.landmark = new int[nrOfLandmark];
			for(int i = 0; i < landmark.length; i++){
				landmark[i] = (int)Math.round(Math.random()*graph.getNodeNr());
			}
			//pre-calculation of ALT
			long sTime = System.currentTimeMillis();
			preCalculationOfALT();
			long eTime = System.currentTimeMillis();
			long time = eTime - sTime;
			time = time / 60000;
			System.out.println("precalculation complete in ["+ time + "] mins.");
		}
	}

	public void setStart(int startId){
		this.start = startId;
	}

	public void setTarget(int targetId){
		this.target = targetId;
	}

	public void compute(){
		for (int i = 0; i < graph.getNodeNr(); i++) {
			g[i] = Double.MAX_VALUE;
			parent[i] = -1; // no parent
		}
		parent[start] = start;
		g[start] = 0.0;
		if(heuristic.equals("Standard")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicStandard(start, target));
		
			
			while(heap.getSize() > 0) {
				double[] min = heap.remove();
				double[] out = graph.getOutgoingEdgesArray((int)min[0]);
				if(out != null){
					for (int i = 0; i < out.length; i += (1+alpha.length)) {
						double[] costVector =  Arrays.copyOfRange(out, i+1, i + 1 + alpha.length);
						if (g[(int)min[0]] + dotProduct(alpha, costVector) < g[(int)out[i]]) {
							g[(int)out[i]] = g[(int)min[0]] + dotProduct(alpha, costVector);
							f[(int)out[i]] = g[(int)out[i]] + heuristicStandard((int)out[i],target);
							parent[(int)out[i]] = (int)min[0];
							if (heap.getPositionInHeap((int)out[i]) != -1) {// in heap
								heap.decreaseKey((int)out[i], f[(int)out[i]]);
							}else {
								heap.add((int)out[i], f[(int)out[i]]);
							}
						}
					}
				}
			}
		}else if(heuristic.equals("ALT")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicALT(start, target));
		
			while(heap.getSize() > 0){
				double[] min = heap.remove();
				double[] out = graph.getOutgoingEdgesArray((int)min[0]);

				if(out != null){
					for (int i = 0; i < out.length; i += (1+alpha.length)) {
						double[] costVector =  Arrays.copyOfRange(out, i+1, i + 1 + alpha.length);
						if (g[(int)min[0]] + dotProduct(alpha, costVector) < g[(int)out[i]]) {
							g[(int)out[i]] = g[(int)min[0]] + dotProduct(alpha, costVector);
							f[(int)out[i]] = g[(int)out[i]] + heuristicALT((int)out[i],target);
							parent[(int)out[i]] = (int)min[0];
							if (heap.getPositionInHeap((int)out[i]) != -1) {// in heap
								heap.decreaseKey((int)out[i], f[(int)out[i]]);
							}else {
								heap.add((int)out[i], f[(int)out[i]]);
							}
						}
					}
				}
			}
		}
		
	}
	
	public void setAlpha(double[] alpha){
		this.alpha = alpha;
	}
	
	public double heuristicStandard(int s, int t) {//h score: euclidean distance
		return Math.sqrt(Math.pow(graph.getLatitude(s) - graph.getLatitude(t), 2.0) + Math.pow(graph.getLongitude(s)-graph.getLongitude(t), 2));
	}
	
	public double heuristicALT(int s, int t){
		double h = lowestCostOfAllLandmarks(s, t, alpha);
		return h;
	}
	
	double dotProduct(double a[], double b[]) {
		double sum = 0;
		for(int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}
	
	public int[] getShortestPathTo(int target) {
		int[] backwardPath = new int[graph.getNodeNr()];
		for(int i = 0; i < backwardPath.length; i++) {
			backwardPath[i] = -1;
		}
		backwardPath[0] = target;
		int i;
		for( i = 1; parent[target] != start; i++) {
			backwardPath[i] = parent[target];
			target = parent[target];
		}
		backwardPath[i] = start;
		return backwardPath;
	}
	
	public String getShortestPathInLonLat(int target){
		int[] path = getShortestPathTo(target);
		int pathLength = 0;
		for(int i = 0; i < path.length; i++) {
			if(path[i] != -1)
				pathLength++;
		}
		double[][] shortestPathInLonLat = new double[pathLength][2];
		for(int i = 0; i < pathLength; i++) {
			shortestPathInLonLat[i][0] = graph.getLongitude(path[i]);
			shortestPathInLonLat[i][1] = graph.getLatitude(path[i]);
		}
		String pathInLonLat = Arrays.deepToString(shortestPathInLonLat);
		return pathInLonLat;
	}

	public boolean strictLessThan(double[] d1, double[] d2){//d1 is less than d2 in at least one component
		int length = d1.length;
		for (int i = 0; i < length; i++) {
			if(d1[i] > d2[i]){
				return false;
			}
		}
		return true;
	}
	/**
	 * check wether d1 is greater than d2 in every component 
	 * @param d1
	 * @param d2
	 * @return
	 */
	private boolean strictGreaterThan(double[] d1, double[] d2){
		boolean greater = true;
		for (int i = 0; i < d2.length; i++) {
			if(d1[i] < d2[i]){
				return false;
			}
		}
		return greater;
	}

	private boolean vectorAreEqual(double[] v1, double[] v2){
		for (int i = 0; i < v2.length; i++) {
			if(v1[i] != v2[i]){
				return false;
			}
		}
		return true;
	}

	// private boolean updateCandidate(int landmarkNr, int nodeNr, double[] newCost){
	// 	boolean update = false;
	// 	boolean replaced = false;
	// 	for(int i = 0; i < landmarkDistance.get(landmarkNr).get(nodeNr).size(); i++){
	// 		if(strictGreaterThan(newCost, landmarkDistance.get(landmarkNr).get(nodeNr).get(i))){
	// 			return false;
	// 		}
	// 		if(strictLessThan(newCost, landmarkDistance.get(landmarkNr).get(nodeNr).get(i))){
	// 			if(replaced){
	// 				landmarkDistance.get(landmarkNr).get(nodeNr).remove(landmarkDistance.get(landmarkNr).get(nodeNr).get(i));
	// 				nrOfCandidate[landmarkNr][nodeNr]--;
	// 				//System.out.println("replaced");
	// 				update = true;
	// 			}else{
	// 				landmarkDistance.get(landmarkNr).get(nodeNr).set(i, newCost);
	// 				replaced = true;
	// 				update = true;
	// 				//System.out.println("seted");
	// 			}
	// 		}
	// 	}
	// 	if(!update){
	// 		landmarkDistance.get(landmarkNr).get(nodeNr).add(newCost);
	// 		nrOfCandidate[landmarkNr][nodeNr]++;
	// 		//System.out.println("added cost");
	// 		update = true;
	// 	}
	// 	return update;
	// }

	// private boolean updateWithFilter(int landmarkNr, int nodeNr, double[] newCost){
	// 	filter = new CHFilter(graph.getNrOFMetrik());
	// 	boolean updated = false;
	// 	for(int i = 0; i < landmarkDistance.get(landmarkNr).get(nodeNr).size(); i++){
	// 		updated = updated || filter.addVertex(landmarkDistance.get(landmarkNr).get(nodeNr).get(i));
	// 	}
	// 	updated = updated || filter.addVertex(newCost);
	// 	ArrayList<double[]> newCandidates = new ArrayList<double[]>(filter.getFilteredVertices());
	// 	Collections.copy(landmarkDistance.get(landmarkNr).get(nodeNr), newCandidates);
	// 	return updated;
	// }

	public void preCalculationOfALT(){
		landmarkDistance = new ArrayList<ArrayList<CHFilter>>();

		for(int lm = 0; lm < nrOfLandmark; lm++){
			ArrayList<CHFilter> nodeList = new ArrayList<>();
			for(int node = 0; node < graph.getNodeNr(); node++){
				CHFilter candiList = new CHFilter(graph.getNrOFMetrik());
				if(isLandmark(node)){
					double[] zero = {0.0, 0.0};
					candiList.addVertex(zero);
					nodeList.add(candiList);
					landmarkDistance.add(nodeList);
				}else{
					double[] infinity = {Double.MAX_VALUE, Double.MAX_VALUE};
					candiList.addVertex(infinity);
					nodeList.add(candiList);
					landmarkDistance.add(nodeList);
				}
			}
		}
		double[][] edgeArray = graph.getEdgeArray();
		for(int landmarkId = 0; landmarkId < nrOfLandmark; landmarkId++){
			boolean[] updated = new boolean[graph.getNodeNr()];
			updated[landmark[landmarkId]] = true;
			ArrayList<double[]> candidates = new ArrayList<double[]>();
			for(int j = 0; j < graph.getNodeNr(); j++){
				boolean globalChange = false;
				boolean[] updateInNextIter = new boolean[graph.getNodeNr()];
				for(int i = 0; i < graph.getEdgeNr(); i++){
					double[] costVector = Arrays.copyOfRange(edgeArray[i], 2, 2+graph.getNrOFMetrik());
					if(updated[(int)edgeArray[i][1]] == true){
						candidates = new ArrayList<double[]>(landmarkDistance.get(landmarkId).get((int)edgeArray[i][1]).getFilteredVertices());
						for(double[] cost: candidates){
							double[] newCost = addTwoVector(cost, costVector);
							//updateInNextIter[(int)edgeArray[i][0]] = updateCandidate(landmarkId, (int)edgeArray[i][0], newCost);
							//updateInNextIter[(int)edgeArray[i][0]] = updateWithFilter(landmarkId, (int)edgeArray[i][0], newCost);
							updateInNextIter[(int)edgeArray[i][0]] = landmarkDistance.get(landmarkId).get((int)edgeArray[i][0]).addVertex(newCost);
							globalChange = globalChange || updateInNextIter[(int)edgeArray[i][0]];
							//System.out.println(Arrays.deepToString(landmarkDistance.get(landmarkId).get((int)edgeArray[i][0]).getFilteredVertices().toArray()));
							
						}
					}
					//System.out.println("node: " + j + "edge: " + i);
				}
				if(globalChange == false){
					System.out.println("precalculation finished");
					break;
				}
				updated = Arrays.copyOf(updateInNextIter, updateInNextIter.length);
				updateInNextIter = new boolean[graph.getNodeNr()];
				if(j % 1 == 0){
					System.out.println("node counter: "+j + " ");//should < number of nodes
				}
			}

		}
	}

	private boolean notInfinity(double[] cost){
		for (int i = 0; i < cost.length; i++) {
			if(cost[i] != Double.MAX_VALUE){
				return true;
			}
		}
		return false;
	}

	private boolean isLandmark(int nodeId){
		for(int i : landmark){
			if(i == nodeId){
				return true;
			}
		}
		return false;
	}

	private int getLandmarkNr(int nodeId){
		if(isLandmark(nodeId)){
			for(int i = 0; i < landmark.length; i++){
				if(landmark[i] == nodeId){
					return i;
				}
			}
		}
		return -1;
	}

	private double[] addTwoVector(double[] v1, double[] v2){
		double[] v3 = new double[v1.length];
		for(int i = 0; i < v1.length; i++){
			v3[i] = v1[i] + v2[i];
		}
		return v3;
	}

	private double lowestHeuristicToOneLandmark(int landmark, int nodeId1, int nodeId2, double[] alpha){
		double lowestCost1 = Double.MAX_VALUE;
		double lowestCost2 = Double.MAX_VALUE;
		double lowestCost;
		for(double[] cost: landmarkDistance.get(landmark).get(nodeId1).getFilteredVertices()){
			if(dotProduct(cost, alpha) < lowestCost1){
				lowestCost1 = dotProduct(cost, alpha);
			}
		}
		for(double[] cost : landmarkDistance.get(landmark).get(nodeId2).getFilteredVertices()){
			if(dotProduct(cost, alpha) < lowestCost2){
				lowestCost2 = dotProduct(cost, alpha);
			}
		}
		lowestCost = Math.abs(lowestCost1 - lowestCost2);
		return lowestCost;
	}

	private double lowestCostOfAllLandmarks(int nodeId1,int nodeId2, double[] alpha){
		double lowestCost = Double.MAX_VALUE;
		for(int i =0; i < landmark.length; i++){
			if(lowestHeuristicToOneLandmark(i, nodeId1, nodeId2, alpha) < lowestCost){
				lowestCost = lowestHeuristicToOneLandmark(i, nodeId1, nodeId2, alpha);
			}
		}
		return lowestCost;
	}
	
	public HashSet<double[]> getLandmarkDistance(int landmarkNr, int nodeId){
		return landmarkDistance.get(landmarkNr).get(nodeId).getFilteredVertices();
	}


	public int[][] getNrOfCandiMaxtrix(){
		return nrOfCandidate;
	}


	public static void main(String[] args) {
		Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/FP/graph-files/bremen.txt");
		int start = 23489;
		int end = 567;
		AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
		aStar.setStart(start);
		aStar.setTarget(end);
		double[] alpha = {0.5, 0.5};
		aStar.setAlpha(alpha);

		System.out.println(Arrays.deepToString(aStar.landmarkDistance.get(0).get(aStar.landmark[0]+1).getFilteredVertices().toArray()));

		long sTime = System.currentTimeMillis();
		aStar.compute();
		long eTime = System.currentTimeMillis();
		long time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");
		System.out.println(aStar.getShortestPathInLonLat(end));

		int maxNrOfCandi = 0;
		int lmWithMaxCandi = 0;
		int nodeWithMaxCandi = 0;
		int[][] nrOfCandiMatrix = aStar.getNrOfCandiMaxtrix();
		for(int i = 0; i < aStar.nrOfLandmark; i++){
			for (int j = 0; j < g.getNodeNr(); j++) {
				if(nrOfCandiMatrix[i][j] > maxNrOfCandi){
					maxNrOfCandi = nrOfCandiMatrix[i][j];
					lmWithMaxCandi = i;
					nodeWithMaxCandi = j;
				}
			}
		}
		System.out.println("max number of candidate:");
		System.out.println(maxNrOfCandi+1);//initialzed as 0, indicate 1
		System.out.println("longest candidate list:");
		System.out.println(Arrays.deepToString(aStar.landmarkDistance.get(lmWithMaxCandi).get(nodeWithMaxCandi).getFilteredVertices().toArray()));
		
	}

	// public static void main(String[] args) {
	// 	CHFilter filter = new CHFilter(2);
	// 	double[] v1 = {Double.MAX_VALUE, Double.MAX_VALUE};
	// 	double[] v2 = {1, 20};
	// 	filter.addVertex(v1);
	// 	filter.addVertex(v2);
	// 	System.out.println(Arrays.deepToString(filter.getFilteredVertices().toArray()));
	// }
}

