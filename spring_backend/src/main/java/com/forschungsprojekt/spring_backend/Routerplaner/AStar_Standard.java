package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;


public class AStar_Standard {
	private Graph graph;
	private double[] f, g;//g: cost of past, f = cost of past + cost of future
	private double[] alpha;
	private int[] parent;
	private int start;
	private int target;
	private String heuristic;
	private int[] landmark;
	private double[][][][] landmarkDistance;
	private int nrOfLandmark;
	private int nrOfCandidate;
	
	public AStar_Standard(Graph graph, int start, int target,  String heuristic, int nrOfLandmark, int nrOfCandidate) {
		this.graph = graph;
		this.f = new double[graph.getNodeNr()];
		this.g = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.start = start;
		this.target = target;
		this.nrOfLandmark = nrOfLandmark;
		this.nrOfCandidate = nrOfCandidate;
		this.heuristic = heuristic;
		if(heuristic.equals("ALT")){
			this.landmark = new int[nrOfLandmark];
			for(int i = 0; i < landmark.length; i++){
				landmark[i] = (int)Math.round(Math.random()*graph.getNodeNr());
			}
			//pre-calculation of ALT
			preCalculationOfALT();
			System.out.println("precalculation complete.");
		}
	}

	public void compute(){
		for (int i = 0; i < graph.getNodeNr(); i++) {
			g[i] = Double.MAX_VALUE;
			parent[i] = -1; // no parent
		}
		
		
		parent[start] = start;
		g[start] = 0;
		if(heuristic.equals("Standard")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicStandard(start, target));
		
			double[] min = heap.remove();
			while(min[0] != target) {
				double[] out = graph.getOutgoingEdgesArray((int)min[0]);
				for (int i = 0; i < out.length; i += (1+alpha.length)) {
					double[] costVector =  Arrays.copyOfRange(out, i+1, i + 1 + alpha.length);
					if (g[(int)min[0]] + dotProduct(alpha, costVector) < g[(int)out[i]]) {
						g[(int)out[i]] = g[(int)min[0]] + dotProduct(alpha, costVector);
						f[(int)out[i]] = g[(int)out[i]] + heuristicStandard((int)min[0],target);
						parent[(int)out[i]] = (int)min[0];
						if (heap.posInHeap[(int)out[i]] != -1) {// in heap
							heap.decreaseKey((int)out[i], f[(int)out[i]]);
						}else {
							heap.add((int)out[i], f[(int)out[i]]);
						}
					}
				}
				min = heap.remove();
			}
		}else if(heuristic.equals("ALT")){
			MinHeap heap = new MinHeap(graph.getNodeNr());
			heap.add(start, g[start] + heuristicALT(start, target));
		
			double[] min = heap.remove();
			while(min[0] != target) {
				double[] out = graph.getOutgoingEdgesArray((int)min[0]);
				for (int i = 0; i < out.length; i += (1+alpha.length)) {
					double[] costVector =  Arrays.copyOfRange(out, i+1, i + 1 + alpha.length);
					if (g[(int)min[0]] + dotProduct(alpha, costVector) < g[(int)out[i]]) {
						g[(int)out[i]] = g[(int)min[0]] + dotProduct(alpha, costVector);
						f[(int)out[i]] = g[(int)out[i]] + heuristicALT((int)min[0],target);
						parent[(int)out[i]] = (int)min[0];
						if (heap.posInHeap[(int)out[i]] != -1) {// in heap
							heap.decreaseKey((int)out[i], f[(int)out[i]]);
						}else {
							heap.add((int)out[i], f[(int)out[i]]);
						}
					}
				}
				min = heap.remove();
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
		double h = Math.abs(lowestCostOfAllLandmarks(s, alpha) - lowestCostOfAllLandmarks(t, alpha));
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
		for(int i = 0; parent[target] != start; i++) {
			backwardPath[i] = parent[target];
			target = parent[target];
		}
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

	public boolean strictLessThan(double[] d1, double[] d2){//d1 is less than d2 in every component
		int length = d1.length;
		for (int i = 0; i < length; i++) {
			if(d1[i] > d2[i]){
				return false;
			}
		}
		return true;
	}

	private void updateCandidate(int landmarkNr, int nodeNr, double[] newCost){
		for(double[] cost : landmarkDistance[landmarkNr][nodeNr]){
			boolean isZero = true;//assume the cost is zero
			for(double i : cost){
				if(i != 0.0){
					isZero = false;
					break;
				}
			}
			if(isZero){
				cost = newCost;
				break;
			}
			else if(strictLessThan(newCost, cost)){
				cost = newCost;
				break;
			}
		}
	}

	private void preCalculationOfALT(){
		landmarkDistance = new double[nrOfLandmark][graph.getNodeNr()][nrOfCandidate][graph.getNrOFMetrik()];
		double[][][] cost = new double[graph.getNodeNr()][graph.maxNrOfOutgoingEdges()][graph.getNrOFMetrik()];
		for(int nodeId = 0; nodeId < graph.getNodeNr(); nodeId++){
			double[] out = graph.getOutgoingEdgesArray(nodeId);
			if(out != null){
				for(int i = 0; i < out.length; i += (1+graph.getNrOFMetrik())){
					int j = 0;
					cost[nodeId][j] = addTwoVector(cost[nodeId][j], Arrays.copyOfRange(out, i+1, i+1+graph.getNrOFMetrik())); 
					if(isLandmark((int)out[i])){
						updateCandidate(getLandmarkNr((int)out[i]), nodeId, cost[nodeId][j]);
					}
					j++;
				}
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

	private double lowestHeuristicFromP2ToP1(int landmark, int nodeId, double[] alpha){
		double lowestCost = Double.MAX_VALUE;
		for(double[] cost: landmarkDistance[landmark][nodeId]){
			if(dotProduct(cost, alpha) < lowestCost){
				lowestCost = dotProduct(cost, alpha);
			}
		}
		return lowestCost;
	}

	private double lowestCostOfAllLandmarks(int nodeId, double[] alpha){
		double lowestCost = Double.MAX_VALUE;
		for(int i =0; i < landmark.length; i++){
			if(lowestHeuristicFromP2ToP1(i, nodeId, alpha) < lowestCost){
				lowestCost = lowestHeuristicFromP2ToP1(i, nodeId, alpha);
			}
		}
		return lowestCost;
	}
	




	public static void main(String[] args) {
		Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/FP/graph-files/map.txt");
		AStar_Standard aStar = new AStar_Standard(g, 23, 233, "ALT", 3, 3);
		double[] alpha = {0.5, 0.5};
		aStar.setAlpha(alpha);
		aStar.compute();
		System.out.println(aStar.getShortestPathInLonLat(233));
	}


}

