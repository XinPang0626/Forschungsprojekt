package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;

public class Dijkstra {
	private double[] dis;
	private int[] parent;
	private double[] alpha;
	
	/**
	 * computes the shortest path given the parameters
	 * @param graph provided "city"
	 * @param start startnode
	 * @param alpha
	 */
	Dijkstra(Graph graph, int start, double[] alpha){
		System.out.println("computing dijkstra...");
		long sTime = System.currentTimeMillis();
		this.dis = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.alpha = alpha;
		
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
	
	double getCostOfShortestPathTo(int nodeID) {
		return this.dis[nodeID];
	}
	double dotProduct(double a[], double b[]) {
		double sum = 0;
		for(int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

}
