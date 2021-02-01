package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;

public class AStar_Standard {
	private Graph graph;
	private double[] f, g;//g: cost of past, f = cost of past + cost of future
//	private double[] alpha;
	private int[] parent;
	private int start;
	private int[] landmark;
	private double[][] landmarkDistance;
	
	public AStar_Standard(Graph graph, int start, int target, double[] alpha, String heuristic, int nrOfLandmark) {
		this.graph = graph;
		this.f = new double[graph.getNodeNr()];
		this.g = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.start = start;
		if(heuristic.equals("ALT")){
			this.landmark = new int[nrOfLandmark];
			for(int i = 0; i < landmark.length; i++){
				landmark[i] = (int)Math.round(Math.random()*graph.getNodeNr());
			}
			//pre-calculation of ALT
			landmarkDistance = new double[nrOfLandmark][graph.getNodeNr()];
			for(int i = 0; i < nrOfLandmark; i++){
				for (int j = 0; j < graph.getNodeNr(); j++) {
					landmarkDistance[i][j] = heuristicStandard(landmark[i], j);
				}
			}
		}
		
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
	
	
	public double heuristicStandard(int s, int t) {//h score: euclidean distance
		return Math.sqrt(Math.pow(graph.getLatitude(s) - graph.getLatitude(t), 2.0) + Math.pow(graph.getLongitude(s)-graph.getLongitude(t), 2));
	}
	public double heuristicALT(int s, int t){
		double h = Double.MAX_VALUE;
		for (int i = 0; i < landmark.length; i++) {
			if(Math.abs(heuristicStandard(landmark[i], t) - heuristicStandard(landmark[i], s)) < h){
				h = Math.abs(heuristicStandard(landmark[i], t) - heuristicStandard(landmark[i], s));
			}
		}
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
	//Test
	// public static void main(String[] args) {
	// Graph graph = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/graph-files/map.txt");
	// double[] alpha = {0.5, 0.5};
	// AStar_Standard astar = new AStar_Standard(graph, 1, 3, alpha);
	// System.out.println(astar.getShortestPathInLonLat(3));
	// }
}

