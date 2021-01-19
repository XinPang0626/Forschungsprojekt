package com.forschungsprojekt.spring_backend.routerplaner;

public class MinHeap {
	private int size;
	private int[] nodeId;
	private double[] cost; // heap
	int[] posInHeap;
	
	MinHeap(int capacity){
		this.size = 0;
		this.nodeId = new int[capacity];
		this.cost = new double[capacity];
		this.posInHeap = new int[capacity];
		
		for (int i = 0; i < capacity; i++) {
			nodeId[i] = -1;
			cost[i] = Integer.MAX_VALUE;
			posInHeap[i] = -1;
		}
	}
	
	void add(int nodeId, double cost) {
		this.nodeId[size] = nodeId;
		this.cost[size] = cost;
		this.posInHeap[nodeId] = size;
		heapifyUP(size);
		size++;
	}
	
	double[] remove() {
		double[] min = {nodeId[0], cost[0]};
		
		if (size != 1) {
			this.posInHeap[nodeId[0]] = -1;
			swap(0, size - 1);
			this.nodeId[size - 1] = -1;
			this.cost[size - 1] = Integer.MAX_VALUE;
			this.posInHeap[nodeId[0]] = 0;
			
		} else if (size == 1) {
			
			this.posInHeap[nodeId[0]] = -1;
			this.nodeId[0] = -1;
			this.cost[0] = Integer.MAX_VALUE;
		}
		size--;
		heapifyDown(0);
		return min;
	}

	private void heapifyUP(int from) {
		int current = from;
		while(cost[current] < cost[parent(current)]) {
			swap(current, parent(current));
			current = parent(current);
		}
		
	}

	private int parent(int pos) {
		return ( pos - 1 ) / 2;
	}
	
	private void swap(int fpos, int spos) {
		double tmp0;
		int tmp1;
		// position or index of node
		posInHeap[nodeId[fpos]] = spos;
		posInHeap[nodeId[spos]] = fpos;
		// swap Id
		tmp1 = nodeId[fpos];
		nodeId[fpos] = nodeId[spos];
		nodeId[spos] = tmp1;
		// swap cost
		tmp0 = cost[fpos];
		cost[fpos] = cost[spos];
		cost[spos] = tmp0;
	}
	
	private boolean isLeaf(int pos) {
		if (pos >= (size / 2) + 1 && pos <= size) {
			return true;
		}
		return false;
	}

	private int rightChild(int pos) {
		return (2 * pos) + 2;
	}

	private int leftChild(int pos) {
		return (2 * pos) + 1;
	}

	private void heapifyDown(int pos) {
		if (!isLeaf(pos)) {
			if (cost[pos] > cost[leftChild(pos)] || cost[pos] > cost[rightChild(pos)]) {
				if (cost[leftChild(pos)] < cost[rightChild(pos)]) {
					swap(pos, leftChild(pos));
					heapifyDown(leftChild(pos));
				}
				else {
					swap(pos, rightChild(pos));
					heapifyDown(rightChild(pos));
				}
			}
		}
	}
	
	public void decreaseKey(int nodeId, double newcost) {
		int pos = this.posInHeap[nodeId];
		if (pos != -1) {
			cost[pos] = newcost;
			heapifyUP(pos);
		}
	}
	
	int getSize() {
		return size;
	}

}
