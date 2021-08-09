package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.Arrays;

public class Dijkstra {
	private double[] dis;
	private int[] parent;
	private double[] alpha;
	private Graph graph;
	private int start;
	private int target;
	private int nrOfVisitedNodes;
	private boolean notAvailable;
	
	/**
	 * computes the shortest path given the parameters
	 * @param graph provided "city"
	 * @param start startnode
	 * @param alpha
	 */
	public Dijkstra(Graph graph, int start, double[] alpha, int target){
		//System.out.println("computing dijkstra...");
		nrOfVisitedNodes = 0;
		long sTime = System.currentTimeMillis();
		this.dis = new double[graph.getNodeNr()];
		this.parent = new int[graph.getNodeNr()];
		this.alpha = alpha;
		this.graph = graph;
		this.start = start;
		this.target = target;
		
		for (int i = 0; i < graph.getNodeNr(); i++) {
			dis[i] = Double.MAX_VALUE;
			parent[i] = -1; // no parent
		}
		
		parent[start] = start;
		dis[start] = 0;
		
		MinHeap heap = new MinHeap(graph.getNodeNr());
		
		heap.add(start, 0);
		
		while(heap.getSize() > 0) {
			double[] min = heap.remove();
			int currentNode = (int)min[0];
			if(currentNode == target){
				break;
			}
			int[] out = graph.getOutgoingEdgesArrayIndex(currentNode);
			
			if(out != null) {
				for (int i = out[0]; i < out[1]; i += (1+alpha.length)) {
					int kinderNode = (int)graph.getCompressedEdgeArray()[i];
					double[] costVector =  Arrays.copyOfRange(graph.getCompressedEdgeArray(), i+1, i+alpha.length + 1);
					if (dis[currentNode] + dotProduct(alpha, costVector) <= dis[kinderNode]) {
						dis[kinderNode] = dis[currentNode] + dotProduct(alpha, costVector);
						parent[kinderNode] = currentNode;
						if (heap.getPositionInHeap(kinderNode) != -1) {// in heap
							heap.decreaseKey(kinderNode, dis[kinderNode]);
						}else {
							heap.add(kinderNode, dis[kinderNode]);
							nrOfVisitedNodes++;
						}
					}
				}
			}
			
		}
		if(parent[target] == -1){
			notAvailable = true;
		}
		long eTime = System.currentTimeMillis();
		long time = eTime - sTime;
		//System.out.println("Dijkstra Computation took ["+time+"] milli seconds");
		
	}

	public double getCost(int node){
		return dis[node];
	}

	public boolean getPathAvailable(){
		return notAvailable;
	}
	
	public int getNrOfVisitedNodes(){
		return nrOfVisitedNodes;
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

	public int[] getShortestPathTo() {
		int[] backwardPath = new int[graph.getNodeNr()];
		for(int i = 0; i < backwardPath.length; i++) {
			backwardPath[i] = -7;
		}
		backwardPath[0] = target;
		int tmp = target;
		int i;
		for( i = 1; parent[tmp] != start; i++) {
			if(parent[tmp] != -1){
				backwardPath[i] = parent[tmp];
				tmp= parent[tmp];
			}else{
				System.out.println("node without parent: "+tmp);
				break;
			}
		}
		backwardPath[i] = start;
		for(int j = 0; j < parent.length; j++){
			parent[j] = -1;
		}
		return backwardPath;
	}
	
	public String getShortestPathInLonLat(){
		int[] path = getShortestPathTo();
		int pathLength = 0;
		for(int i = 0; i < path.length; i++) {
			if(path[i] != -7){
				pathLength++;
			}else{
				break;
			}
		}
		double[][] shortestPathInLonLat = new double[pathLength][2];
		for(int i = 0; i < pathLength; i++) {
			shortestPathInLonLat[i][0] = graph.getLongitude(path[i]);
			shortestPathInLonLat[i][1] = graph.getLatitude(path[i]);
		}
		String pathInLonLat = Arrays.deepToString(shortestPathInLonLat);
		return pathInLonLat;
	}

	public static void main(String[] args) {
		Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/FP/graph-files/bremen.txt");
		boolean pathNotFound = true;
		while(pathNotFound){
			//int start = (int) (Math.random() * g.getNodeNr());	
			int start = 72434;	
			//int target = (int) (Math.random() * g.getNodeNr());
			int target = 96606;
			System.out.println("nr ofoutgoing edge: "+g.getNrOfOutgoingEdges(start));
			System.out.println("start: " + start);
			System.out.println("target: " + target);
			double[] alpha = {0.5, 0.5};
			Dijkstra dij = new Dijkstra(g, start, alpha, target);
			System.out.println("not available: "+dij.getPathAvailable());
			if(dij.notAvailable == false){
				System.out.println(dij.getShortestPathInLonLat());
				break;
			}
		}

		// int start = 45748;		
		// int target = 48038;
		// System.out.println("nr ofoutgoing edge: "+g.getNrOfOutgoingEdges(start));
		// System.out.println("start: " + start);
		// System.out.println("target: " + target);
		// double[] alpha = {0.5, 0.5};
		// Dijkstra dij = new Dijkstra(g, start, alpha, target);
		// System.out.println("not available: "+dij.getPathAvailable());
		// System.out.println(dij.getShortestPathInLonLat());
	}
}
