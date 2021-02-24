package com.forschungsprojekt.spring_backend.routerplaner;

public class MinHeap {
	private int size;
	private int[] nodeIdAt;//indicate the nodeId at correspond index, equals -1 if node not in heap
	private double[] cost; // indicate the cost of the node/given index
	private int[] posInHeap;//indicate the heap position/index of a nodeid
	/**
	 * Constructor of heap with given size
	 * @param capacity : int number, the given size
	 */
	MinHeap(int capacity){
		this.size = 0;//how many element in heap
		this.nodeIdAt = new int[capacity];
		this.cost = new double[capacity];
		this.posInHeap = new int[capacity];
		
		for (int i = 0; i < capacity; i++) {
			nodeIdAt[i] = -1;//heap is empty
			cost[i] = Double.MAX_VALUE;
			posInHeap[i] = -1;
		}
	}
	/**
	 * Add a unseen node in the heap 
	 * @param nodeId: int number, id of the node
	 * @param cost: double number, cost from start point to this node
	 */
	void add(int nodeId, double cost) {
		this.nodeIdAt[size] = nodeId;
		this.cost[size] = cost;
		this.posInHeap[nodeId] = size;
		heapifyUP(size);
		size++;
	}
	
	public int getPositionInHeap(int nodeId){
		return posInHeap[nodeId];
	}

	double[] remove() {
		double[] min = {nodeIdAt[0], cost[0]};
		
		if (size != 1) {// more than 1 element in heap
			
			swap(0, size - 1);
			this.posInHeap[nodeIdAt[size - 1]] = -1;
			this.nodeIdAt[size - 1] = -1;
			this.cost[size - 1] = Double.MAX_VALUE;
			size--;
			heapifyDown(0);
		} else {//last element in heap is removed
			
			this.posInHeap[nodeIdAt[0]] = -1;
			this.nodeIdAt[0] = -1;
			this.cost[0] = Double.MAX_VALUE;
			size--;
		}
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
		if(fpos == -1 || spos == -1){
			System.out.println("not in heap");
		}
		double tmp0;
		int tmp1;
		// position or index of node
		posInHeap[nodeIdAt[fpos]] = spos;
		posInHeap[nodeIdAt[spos]] = fpos;
		// swap Id
		tmp1 = nodeIdAt[fpos];
		nodeIdAt[fpos] = nodeIdAt[spos];
		nodeIdAt[spos] = tmp1;
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
		if(pos == -1){
			add(nodeId, newcost);
		}
		if(pos != -1){
			cost[pos] = newcost;
			heapifyUP(pos);
		}
	}
	
	int getSize() {
		return size;
	}

}
