package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;

public class AStar_Standard {
	private Graph graph;
	private double[] f, g;//f=g+h
//	private double[] alpha;
	private int[] parent;
	private int start;
	
	public AStar_Standard(Graph graph, int start, int target, double[] alpha) {
		this.graph = graph;
		this.f = new double[graph.getNodeNr()];
		this.g = new double[graph.getNodeNr()];
		this.start = start;
		//this.alpha = alpha;
		
		for (int i = 0; i < graph.getNodeNr(); i++) {
			g[i] = Double.MAX_VALUE;
			parent[i] = -1; // no parent
		}
		
		
		parent[start] = start;
		g[start] = 0;
		
		MinHeap heap = new MinHeap(graph.getNodeNr());
		heap.add(start, g[start] + heuristic(start, target));
		
		double[] min = heap.remove();
		while(min[0] != target) {
			double[] out = graph.getOutgoingEdgesArray((int)min[0]);
			
			
			
			for (int i = 0; i < out.length; i += (1+alpha.length)) {
				double[] costVector =  Arrays.copyOfRange(out, i+1, i + 1 + alpha.length);
				if (g[(int)min[0]] + dotProduct(alpha, costVector) < g[(int)out[i]]) {
					g[(int)out[i]] = g[(int)min[0]] + dotProduct(alpha, costVector);
					f[(int)out[i]] = g[(int)out[i]] + heuristic((int)min[0],target);
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
	
	
	public double heuristic(int s, int t) {//h score: euclidean distance
		return Math.sqrt(Math.pow(graph.getLatitude(s) - graph.getLatitude(t), 2.0) + Math.pow(graph.getLongitude(s)-graph.getLongitude(t), 2));
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
	
	
}
