package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.ArrayList;
import java.util.Arrays;

public class AStar_Standard {
	private Graph graph;
	private double f;
	private double[] g;//g: cost of past, f = cost of past + cost of future
	private double[] alpha;
	private int[] parent;
	private int start;
	private int target;
	private String heuristic;
	private int[] landmark;
	private ArrayList<ArrayList<ArrayList<double[]>>> landmarkDistance;
	private int nrOfLandmark;
	private int[][] nrOfCandidate;
	private CHFilter filter;
	private ArrayList<Integer> modifiedNode;
	private boolean targetNodeKLT;
	private double[] targetToLandmarkKLT;
	
	public AStar_Standard(Graph graph, String heuristic, int nrOfLandmark) {
		this.graph = graph;
		this.f = 0.0;
		this.g = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.nrOfLandmark = nrOfLandmark;
		this.nrOfCandidate = new int[nrOfLandmark][graph.getNodeNr()];//initialize with zero's
		this.heuristic = heuristic;
		modifiedNode = new ArrayList<>();
		for (int i = 0; i < graph.getNodeNr(); i++) {
			modifiedNode.add(i);
		}
		
		
		if(heuristic.equals("ALT")){
			this.landmark = new int[nrOfLandmark];
			this.targetToLandmarkKLT = new double[nrOfLandmark];
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
		for (int i = 0; i < modifiedNode.size(); i++) {
			g[modifiedNode.get(i)] = Double.MAX_VALUE;
			parent[modifiedNode.get(i)] = -1;
		}
		modifiedNode = new ArrayList<>();
		parent[start] = start;
		g[start] = 0.0;
		modifiedNode.add(start);
		if(heuristic.equals("Standard")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicStandard(start, target));
			boolean foundTarget = false;
			
			while(heap.getSize() > 0 && !foundTarget) {
				double[] min = heap.remove();
				int currentNode = (int)min[0];
				if(currentNode == target){
					foundTarget = true;
				}
				int[] out = graph.getOutgoingEdgesArrayIndex(currentNode);
				if(out != null){
					for (int i = out[0]; i < out[1]; i += (1+alpha.length)) {
						double[] costVector =  Arrays.copyOfRange(graph.getCompressedEdgeArray(), i+1, i + 1 + alpha.length);
						int kinderNode = (int)graph.getCompressedEdgeArray()[i];
						if (g[currentNode] + dotProduct(alpha, costVector) < g[kinderNode]) {
							g[kinderNode] = g[currentNode]+ dotProduct(alpha, costVector);
							modifiedNode.add(kinderNode);
							f = g[kinderNode] + heuristicStandard(kinderNode,target);
							parent[kinderNode] = currentNode;
							if (heap.getPositionInHeap(kinderNode) != -1) {// in heap
								heap.decreaseKey(kinderNode, f);
							}else {
								heap.add(kinderNode, f);
							}
						}
					}
				}
			}
		}else if(heuristic.equals("ALT")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicALT(start, target));
			boolean foundTarget = false;
			while(heap.getSize() > 0 && !foundTarget){
				double[] min = heap.remove();
				int currentNode = (int)min[0];
				if(currentNode == target){
					foundTarget = true;
				}
				int[] out = graph.getOutgoingEdgesArrayIndex(currentNode);

				if(out != null){
					for (int i = out[0]; i < out[1]; i += (1+alpha.length)) {
						double[] costVector =  Arrays.copyOfRange(graph.getCompressedEdgeArray(), i+1, i + 1 + alpha.length);
						int kinderNode = graph.getTargetNodeArray()[i];
						if (g[currentNode] + dotProduct(alpha, costVector) < g[kinderNode]) {
							g[kinderNode] = g[currentNode] + dotProduct(alpha, costVector);
							modifiedNode.add(kinderNode);
							f = g[kinderNode] + heuristicALT(kinderNode,target);
							parent[kinderNode] = currentNode;
							if (heap.getPositionInHeap(kinderNode) != -1) {// in heap
								heap.decreaseKey(kinderNode, f);
							}else {
								heap.add(kinderNode, f);
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
		double h = greatestCostOfAllLandmarks(s, t, alpha);
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
		int tempParent = target;
		int i;
		for( i = 1; parent[tempParent] != start; i++) {
			backwardPath[i] = parent[tempParent];
			tempParent= parent[tempParent];
		}
		backwardPath[i] = start;
		return backwardPath;
	}
	
	public String getShortestPathInLonLat(int target){
		int[] path = getShortestPathTo(target);
		int pathLength = 0;
		for(int i = 0; i < path.length && path[i] != -1; i++) {
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





	public boolean updateWithFilter(int landmarkNr, int nodeNr, double[] newCost){
		filter = new CHFilter(graph.getNrOFMetrik());
		boolean updated = false;
		for(int i = 0; i < landmarkDistance.get(landmarkNr).get(nodeNr).size(); i++){
			filter.addVertex(landmarkDistance.get(landmarkNr).get(nodeNr).get(i));
		}
		updated = filter.addVertex(newCost);
		if(updated == true){
			ArrayList<double[]> newCandidates = new ArrayList<double[]>(filter.getFilteredVertices());
			landmarkDistance.get(landmarkNr).set(nodeNr, newCandidates);
			nrOfCandidate[landmarkNr][nodeNr] = newCandidates.size();
		}
		return updated;
	}

	public void preCalculationOfALT(){
		landmarkDistance = new ArrayList<ArrayList<ArrayList<double[]>>>();

		for(int lm = 0; lm < nrOfLandmark; lm++){
			ArrayList<ArrayList<double[]>> nodeList = new ArrayList<ArrayList<double[]>>();
			for(int node = 0; node < graph.getNodeNr(); node++){
				ArrayList<double[]> candiList = new ArrayList<double[]>();
				if(isLandmark(node)){
					double[] zero = new double[graph.getNrOFMetrik()];
					Arrays.fill(zero, 0.0);
					candiList.add(zero);
					nodeList.add(candiList);
					landmarkDistance.add(nodeList);
				}else{
					double[] infinity = new double[graph.getNrOFMetrik()];
					Arrays.fill(infinity, Double.MAX_VALUE);
					candiList.add(infinity);
					nodeList.add(candiList);
					landmarkDistance.add(nodeList);
				}
			}
		}
		for(int landmarkId = 0; landmarkId < nrOfLandmark; landmarkId++){
			boolean[] updated = new boolean[graph.getNodeNr()];
			updated[landmark[landmarkId]] = true;
			ArrayList<double[]> candidates = new ArrayList<double[]>();
			for(int j = 0; j < graph.getNodeNr(); j++){
				System.out.println("current loop number: " + j);
				boolean globalChange = false;
				boolean[] updateInNextIter = new boolean[graph.getNodeNr()];
				for(int i = 0; i < graph.getNodeNr(); i++){
					int[] out = graph.getOutgoingEdgesArrayIndex(i);
					if(out != null){
						int edgeBegin = i;
						for( int m = out[0]; m < out[1]; m += (1+graph.getNrOFMetrik())){
							int edgeEnd = (int)graph.getCompressedEdgeArray()[m];
							double[] costVector =  Arrays.copyOfRange(graph.getCompressedEdgeArray(), m+1, m + 1 + graph.getNrOFMetrik());
							if(updated[edgeEnd] == true){
								candidates = landmarkDistance.get(landmarkId).get(edgeEnd);
								for(double[] cost: candidates){
									double[] newCost = addTwoVector(cost, costVector);
									updateInNextIter[edgeBegin] = updateInNextIter[edgeBegin] || updateWithFilter(landmarkId, edgeBegin, newCost);									// }
								}
							}
							globalChange = globalChange || updateInNextIter[edgeBegin];
						}
					}
				}
				if(globalChange == false){
					System.out.println("no more global changes");
					break;
				}
				updated = Arrays.copyOf(updateInNextIter, updateInNextIter.length);
			}
		}
	}


	private boolean isLandmark(int nodeId){
		for(int i : landmark){
			if(i == nodeId){
				return true;
			}
		}
		return false;
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
		
		if(nodeId1 == target && !targetNodeKLT){
			for(double[] cost: landmarkDistance.get(landmark).get(nodeId1)){
				if(dotProduct(cost, alpha) < lowestCost1){
					lowestCost1 = dotProduct(cost, alpha);
				}
			}
			targetNodeKLT = true;
			targetToLandmarkKLT[landmark] = lowestCost1;
		}else if(nodeId1 == target && targetNodeKLT){
			lowestCost1 = targetToLandmarkKLT[landmark];
		}else{
			for(double[] cost: landmarkDistance.get(landmark).get(nodeId1)){
				if(dotProduct(cost, alpha) < lowestCost1){
					lowestCost1 = dotProduct(cost, alpha);
				}
			}
		}


		if(nodeId2 == target && !targetNodeKLT){
			for(double[] cost : landmarkDistance.get(landmark).get(nodeId2)){
				if(dotProduct(cost, alpha) < lowestCost2){
					lowestCost2 = dotProduct(cost, alpha);
				}
			}
			targetNodeKLT = true;
			targetToLandmarkKLT[landmark] = lowestCost2;
		}else if(nodeId2 == target && targetNodeKLT){
			lowestCost2 = targetToLandmarkKLT[landmark];
		}else{
			for(double[] cost : landmarkDistance.get(landmark).get(nodeId2)){
				if(dotProduct(cost, alpha) < lowestCost2){
					lowestCost2 = dotProduct(cost, alpha);
				}
			}
		}

		lowestCost = Math.abs(lowestCost1 - lowestCost2);
		return lowestCost;
	}

	private double greatestCostOfAllLandmarks(int nodeId1,int nodeId2, double[] alpha){
		double greatestCost = Double.MIN_VALUE;
		for(int i =0; i < landmark.length; i++){
			if(lowestHeuristicToOneLandmark(i, nodeId1, nodeId2, alpha) > greatestCost){
				greatestCost = lowestHeuristicToOneLandmark(i, nodeId1, nodeId2, alpha);
			}
		}
		return greatestCost;
	}
	
	public ArrayList<double[]> getLandmarkDistance(int landmarkNr, int nodeId){
		return landmarkDistance.get(landmarkNr).get(nodeId);
	}


	public int[][] getNrOfCandiMaxtrix(){
		return nrOfCandidate;
	}

	public void getMaxCandidatesArray(){
		int maxNrOfCandi = 0;
		int lmWithMaxCandi = 0;
		int nodeWithMaxCandi = 0;
		int[][] nrOfCandiMatrix = getNrOfCandiMaxtrix();
		for(int i = 0; i < nrOfLandmark; i++){
			for (int j = 0; j < graph.getNodeNr(); j++) {
				if(nrOfCandiMatrix[i][j] > maxNrOfCandi){
					maxNrOfCandi = nrOfCandiMatrix[i][j];
					lmWithMaxCandi = i;
					nodeWithMaxCandi = j;
				}
			}
		}
		System.out.println("max number of candidate:");
		System.out.println(maxNrOfCandi);
		System.out.println("longest candidate list:");
		System.out.println(Arrays.deepToString(landmarkDistance.get(lmWithMaxCandi).get(nodeWithMaxCandi).toArray()));
	}

	public static void main(String[] args) {
		Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/FP/graph-files/bremen.txt");
		int start = 23489;
		int end = 11111;
		AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
		aStar.setStart(start);
		aStar.setTarget(end);
		double[] alpha = {0.5, 0.5};
		aStar.setAlpha(alpha);

		//System.out.println(Arrays.deepToString(aStar.landmarkDistance.get(0).get(aStar.landmark[0]+1).toArray()));

		long sTime = System.currentTimeMillis();
		aStar.compute();
		long eTime = System.currentTimeMillis();
		long time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");
		System.out.println(aStar.getShortestPathInLonLat(end));

		start = 12312;
		end = 34235;
		aStar.setStart(start);
		aStar.setTarget(end);
		sTime = System.currentTimeMillis();
		aStar.compute();
		eTime = System.currentTimeMillis();
		time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");
		System.out.println(aStar.getShortestPathInLonLat(end));

		start = 4712;
		end = 22721;
		aStar.setStart(start);
		aStar.setTarget(end);
		sTime = System.currentTimeMillis();
		aStar.compute();
		eTime = System.currentTimeMillis();
		time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");
		System.out.println(aStar.getShortestPathInLonLat(end));

		aStar.getMaxCandidatesArray();
		
		
	}
}

