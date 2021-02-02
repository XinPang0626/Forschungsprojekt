package com.forschungsprojekt.spring_backend.localization;

import java.util.Arrays;

import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;
public class Quadtree {
    public static int numberOfinsertedNode = 0;
	private final int MAX_CAPACITY = 100;
	private int size = 0;
	private int level = 0;
	private int[] id;
	private double[] latitude;
	private double[] longtiude;
	private Boundary boundary;
	private Quadtree northEast = null;
	private Quadtree northWest = null;
	private Quadtree southEast = null;
	private Quadtree southWest = null;
    private static double dis;
    private static Graph graph;

	public Quadtree(int level, Boundary boundary,double distance) {
		this.level = level;
		this.boundary = boundary;
		id = new int[MAX_CAPACITY];
		latitude = new double[MAX_CAPACITY];
		longtiude = new double[MAX_CAPACITY];
		dis = distance;
    }
    public Quadtree(String path) {
        graph = new Graph(path);
        double latMin = graph.getLatitude(0);
		double latMax = graph.getLatitude(0);
		double longMin = graph.getLongitude(0);
		double longMax = graph.getLongitude(0);

		for (int i = 1; i < graph.getNodeNr(); i++) {
			if (graph.getLatitude(i) < latMin) {
				latMin = graph.getLatitude(i);
			}
		}
		for (int i = 1; i < graph.getNodeNr(); i++) {
			if (graph.getLatitude(i) > latMax) {
				latMax = graph.getLatitude(i);
			}
			
		}
		for (int i = 1; i < graph.getNodeNr(); i++) {
			if (graph.getLongitude(i) < longMin) {
				longMin = graph.getLongitude(i);
			}
		}
		for (int i = 1; i < graph.getNodeNr(); i++) {
			if (graph.getLongitude(i) > longMax) {
				longMax = graph.getLongitude(i);
			}
        }
        this.level = 1;
        this.boundary = new Boundary(latMin, longMin, latMax, longMax);
        dis = Double.MAX_VALUE;
        id = new int[MAX_CAPACITY];
		latitude = new double[MAX_CAPACITY];
        longtiude = new double[MAX_CAPACITY];
        
        for (int i = 0; i < graph.getNodeNr(); i++) {
			insert(i, graph.getLatitude(i), graph.getLongitude(i));
		}
    }

	public void insert(int id, double latitude, double longtiude) {
		if (!this.boundary.inRange(latitude, longtiude)) {
			return;
		}

		if (size < MAX_CAPACITY) {
			this.id[size] = id;
			this.latitude[size] = latitude;
			this.longtiude[size] = longtiude;
			size++;
			numberOfinsertedNode++;
			return;
		}
		// Exceeded the capacity so split it in FOUR
		if (northWest == null) {
			split();
		}

		// Check coordinates belongs to which partition
		if (this.northWest.boundary.inRange(latitude, longtiude)) {
			this.northWest.insert(id, latitude, longtiude);
		}

		else if (this.northEast.boundary.inRange(latitude, longtiude)) {
			this.northEast.insert(id, latitude, longtiude);
		}

		else if (this.southWest.boundary.inRange(latitude, longtiude)) {
			this.southWest.insert(id, latitude, longtiude);
		}

		else if (this.southEast.boundary.inRange(latitude, longtiude)) {
			this.southEast.insert(id, latitude, longtiude);
		}

		else {
			System.out.println("ERROR");
		}

	}

	void split() {
		double xOffset = this.boundary.getxMin() + (this.boundary.getxMax() - this.boundary.getxMin()) / 2;
		double yOffset = this.boundary.getyMin() + (this.boundary.getyMax() - this.boundary.getyMin()) / 2;

		northWest = new Quadtree(this.level + 1,
				new Boundary(this.boundary.getxMin(), yOffset, xOffset, this.boundary.getyMax()),dis);
		northEast = new Quadtree(this.level + 1,
				new Boundary(xOffset, yOffset, this.boundary.getxMax(), this.boundary.getyMax()),dis);
		southWest = new Quadtree(this.level + 1,
				new Boundary(this.boundary.getxMin(), this.boundary.getyMin(), xOffset, yOffset),dis);
		southEast = new Quadtree(this.level + 1,
				new Boundary(xOffset, this.boundary.getyMin(), this.boundary.getxMax(), yOffset),dis);

	}
	
	
	public int nextNeighbor(double x, double y) {
		//Stack<QuadTree> stack = new Stack<>();
		Quadtree[] stack = new Quadtree[4];
		int nearestNeighbor = -1;
		for(int i = 0; i < this.size; i++) {
			if(distance(this.latitude[i],this.longtiude[i],x,y)<dis) {
				dis = distance(this.latitude[i],this.longtiude[i],x,y);
				nearestNeighbor = this.id[i];
			}
		}
		Circle c = new Circle(x,y,dis);
		
		if(this.northEast != null) {
//			stack.push(this.northEast);
//			stack.push(this.northWest);
//			stack.push(this.southEast);
//			stack.push(this.southWest);
			stack[0] = this.northEast;
			stack[1] = this.northWest;
			stack[2] = this.southEast;
			stack[3] = this.southWest;
		}
		while(!(stack[0] == null && stack[1] == null && stack[2] == null && stack[3] == null)) {
			Quadtree tr = null;
			for (int i = 0; i < 4; i++) {
				if(stack[i] != null) {
					tr = stack[i];
					stack[i] = null;
					break;
				}
			}
			if(c.intersect(tr.boundary)) {
				int nearestNeighbor1 = tr.nextNeighbor(x, y);
				if(nearestNeighbor1 == -1) {
					continue;
				}
				nearestNeighbor = nearestNeighbor1;
				c.setRadius(dis);
			}
        }
        dis = Double.MAX_VALUE;
		return nearestNeighbor;
	}
	// public int nextNeighbor2(double x,double y) {
	// 	int i = nextNeighbor(x,y);
	// 	dis = Double.MAX_VALUE;
	// 	return i;
	// }
	
	double distance(double x1,double y1,double x2,double y2) {
		double lat_diff_pow_2 = Math.pow(x1  - x2,  2);
    	double lon_diff_pow_2 = Math.pow(y1 - y2, 2);
    	double diff = Math.sqrt(lat_diff_pow_2 + lon_diff_pow_2);
		return diff;
	}
	public Graph getGraph(){
		return graph;
	}

	public static void main(String[] args) {
		// Quadtree q = new Quadtree("/Users/xinpang/Desktop/Studium/5. Semester/FP/graph-files/map.txt");
		// int start = q.nextNeighbor(42.1, 53.2);
		// System.out.println("start: "+start);
		// int end = q.nextNeighbor(58.8, 30.2);
		// System.out.println("end: "+end);
		// double[] alpha = {0.5, 0.5};
		// Dijkstra dij = new Dijkstra(q.getGraph(), start, alpha);
		// System.out.println(dij.getShortestPathInLonLat(end));
		
	}
}
