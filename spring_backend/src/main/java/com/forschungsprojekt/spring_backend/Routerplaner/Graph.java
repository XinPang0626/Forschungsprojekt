package com.forschungsprojekt.spring_backend.routerplaner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * read static Graph date from File represent it as Arrays
 * 
 * 
 */
public class Graph {
	private int nodeNr;
	private int edgeNr;
	private int nrOfMetrik;
	private int lengthOfEdgeElement;//Edge Element consist of endNode and metric vector
	private double[] latitude;
	private double[] longitude;
	private int[] nrOfOutgoingEdges;
	private double[] compressedEdgeArray;
	private double[][] edgeArray;
	private int[] nodeArray;// save the corrosponding edge index of edgeArray

	public Graph(String path) {
		System.out.println();
		System.out.print("Reading file ");
		long startTime = System.currentTimeMillis();
		readGraphFile(path);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println();
		System.out.println("Reading file took [" + totalTime +"] milliseconds");
	}

	private void readGraphFile(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while(line.startsWith("#", 0)) {//skip the commentar
				line = br.readLine();
			}

			nrOfMetrik = Integer.parseInt(br.readLine());// read the first non-commentar line after the empty line
			lengthOfEdgeElement = 1 + nrOfMetrik;
			nodeNr = Integer.parseInt(br.readLine());//read the node number
			edgeNr = Integer.parseInt(br.readLine());// read the edge number
			latitude = new double[nodeNr];
			longitude = new double[nodeNr];
			nrOfOutgoingEdges = new int[nodeNr];
			compressedEdgeArray = new double[edgeNr * lengthOfEdgeElement];
			nodeArray = new int[nodeNr];
			edgeArray = new double[edgeNr][2 + nrOfMetrik];
			
			// fill nodeArray
			for (int i = 0; i < nodeNr; i++) {// read lines of all nodes
				if (i % 1000000 == 0){
					System.out.print("#");
				}
				line = br.readLine();
				String[] tempString = line.split(" ");
				latitude[i] = Double.valueOf(tempString[2]);
				longitude[i] = Double.valueOf(tempString[3]);
				nodeArray[i] = -1;
			}
			// fill edgeArray and compressedEdgeArray
			int index = 0;
			int startNode = -1;
			int newStartNode = -1;
			int endNode = -1;
			for (int i = 0; i < edgeNr; i++) {// read line of all edges
				if (i % 1000000 == 0){
					System.out.print("#");
				}
				line = br.readLine();
				
				String[] tempStringArray = line.split(" ");
				newStartNode = Integer.parseInt(tempStringArray[0]);
				if (startNode != newStartNode){//if comes an edge with new starting node
					nodeArray[newStartNode] = index;// then save the index to the nodearray
					startNode = newStartNode;// and update the startnode
				}
				endNode = Integer.parseInt(tempStringArray[1]);
				nrOfOutgoingEdges[startNode]++;//count the number of outgoing edges
				compressedEdgeArray[index] = endNode;
				index++;
				
				edgeArray[i][0] = newStartNode;
				edgeArray[i][1] = endNode;
				for(int j = 0; j < nrOfMetrik;j++){
					edgeArray[i][j+2] = Double.parseDouble(tempStringArray[2 + j]);
				}

				for (int j = 0; j < nrOfMetrik; j++) {//add metric in edgearray
					compressedEdgeArray[index] = Double.parseDouble(tempStringArray[2 + j]);
					index++;
				}
			}
			
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	double[] getCompressedEdgeArray() {
		return compressedEdgeArray;
	}

	int[] getNodeArray() {
		return nodeArray;
	}

	double[][] getEdgeArray(){
		return edgeArray;
	}

	int getNrOfOutgoingEdges(int nodeID) {
		return nrOfOutgoingEdges[nodeID];
	}

	public int getNodeNr() {
		return nodeNr;
	}

	int getEdgeNr() {
		return edgeNr;
	}

	public double getLatitude(int nodeID) {
		return latitude[nodeID];
	}

	public double getLongitude(int nodeID) {
		return longitude[nodeID];
	}

	/**
	 * 
	 * @param nodeID
	 * 
	 */
	public double[] getOutgoingEdgesArray(int nodeID) {
		if (nrOfOutgoingEdges[nodeID] >= 1) {
			int startIndex = nodeArray[nodeID];
			int endIndex = getNrOfOutgoingEdges(nodeID) * lengthOfEdgeElement + startIndex;
			return Arrays.copyOfRange(compressedEdgeArray, startIndex, endIndex);
		}
		return null;
	}

	public int[] getNeighbours(int nodeId){
		int[] neighbours = new int[getNrOfOutgoingEdges(nodeId)];
		double[] out = getOutgoingEdgesArray(nodeId);
		for (int i = 0; i < out.length; i+=lengthOfEdgeElement) {
			int j = 0;
			neighbours[j] = (int)out[i];
			j++;
		}
		return neighbours;
	}

	/**
	 * to return the number of outgoing edges for specific node
	 * 
	 * @param nodeID
	 * @return the number of outgoing edges for the node
	 */
	public int getNumberOfOutgoingEdge(int nodeID) {
		return nrOfOutgoingEdges[nodeID];
	}

	public int getNrOFMetrik(){
		return nrOfMetrik;
	}

	public int maxNrOfOutgoingEdges(){
		int max = 0;
		for(int i = 0; i < nodeNr; i++){
			if(getNrOfOutgoingEdges(i) > max){
				max = getNrOfOutgoingEdges(i);
			}
		}
		return max;
	}

}
