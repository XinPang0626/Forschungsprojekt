package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;

public class Dijkstra {
	private double[] dis;
	private int[] parent;
	private double[] alpha;
	private Graph graph;
	private int start;
	
	/**
	 * computes the shortest path given the parameters
	 * @param graph provided "city"
	 * @param start startnode
	 * @param alpha
	 */
	public Dijkstra(Graph graph, int start, double[] alpha){
		System.out.println("computing dijkstra...");
		long sTime = System.currentTimeMillis();
		this.dis = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.alpha = alpha;
		this.graph = graph;
		this.start = start;
		
		for (int i = 0; i < parent.length; i++) {
			dis[i] = Double.MAX_VALUE;
			parent[i] = -1; // no parent
		}
		
		parent[start] = start;
		dis[start] = 0;
		
		MinHeap heap = new MinHeap(graph.getNodeNr());
		
		heap.add(start, 0);
		
		while(heap.getSize() > 0) {
			double[] min = heap.remove();
			double[] out = graph.getOutgoingEdgesArray((int)min[0]);
			
			if(out != null) {
				for (int i = 0; i < out.length; i += (1+alpha.length)) {
					double[] costVector =  Arrays.copyOfRange(out, i+1, i+alpha.length + 1);
					if (dis[(int)min[0]] + dotProduct(alpha, costVector) < dis[(int)out[i]]) {
						dis[(int)out[i]] = dis[(int)min[0]] + dotProduct(alpha, costVector);
						parent[(int)out[i]] = (int)min[0];
						if (heap.posInHeap[(int)out[i]] != -1) {// in heap
							heap.decreaseKey((int)out[i], dis[(int)out[i]]);
						}else {
							heap.add((int)out[i], dis[(int)out[i]]);
						}
					}
				}
				
				
			}
			
		}
		long eTime = System.currentTimeMillis();
		long time = eTime - sTime;
		System.out.println("Dijkstra Computation took ["+time+"] milli seconds");
		
	}
	
	public double getCostOfShortestPathTo(int nodeID) {
		return this.dis[nodeID];
	}
	public double dotProduct(double a[], double b[]) {
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
