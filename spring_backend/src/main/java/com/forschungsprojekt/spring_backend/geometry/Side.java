package com.forschungsprojekt.spring_backend.geometry;

import static com.forschungsprojekt.spring_backend.routerplaner.CHFilter.DIM;

import java.util.Arrays;

//Copyright: Felix Weitbrecht, Universität Stuttgart
public class Side {

	// whether this is a reverse side (matters for orientation tests)
	public boolean isReverse = false;

	// the vertices of this side
	public double[][] vertices;

	// backside
	public Side r;

	// the simplex bounded by this side
	public AbstractSimplex simplex;

	// If side is part of the hull (i.e. not part of a simplex), these are
	// its neighboring sides on the hull. Old hull pointers aren't removed.
	// convention: DIM-2-simplex opposite vertex i is handled at index i
	public Side hNeighbors[] = new Side[DIM];

	// whether this side is visible from the provided origin vertex
	private boolean relevant = false;
	private int relevantVersion = -1; // for caching

	public Side(double[][] vertices) {
		this.vertices = vertices;
		Side sideR = new Side(vertices, true);
		this.r = sideR;
		sideR.r = this;
	}

	private Side(double[][] vertices, boolean isReverse) {
		this.vertices = vertices;
		this.isReverse = isReverse;
	}

	public boolean isRelevant(double[] vOrigin, int originVersion) {
		if (originVersion > relevantVersion) {
			// update cache
			relevant = facesVertex(vOrigin);
			relevantVersion = originVersion;
		}
		return relevant;
	}

	// creates a new side sharing a DIM-2-simplex with this side, facing this
	// side, but with vNew instead of pFacing
	public Side createSideFacing(double[] vNew, int vFacingIdx) {
		// copy all vertices of sideNeighbor, but replace vFacing with vNew
		double[][] verticesNew = Arrays.copyOf(vertices, DIM);
		verticesNew[vFacingIdx] = vNew;
		Side sideNew = new Side(verticesNew);
		// return reversed so new side faces old side
		return isReverse ? sideNew : sideNew.r;
	}

	// returns the non-open simplex attached to this side, null if there's none
	public Simplex simplex() {
		return simplex instanceof Simplex ? (Simplex) simplex : null;
	}

	// returns the hull side linked opposite the given vertex.
	// no guarantees made for non-hull sides and vertices not part of this side.
	public Side hNeighborOpposite(double[] vOpposite) {
		for (int i = 0; i < DIM; i++) {
			if (vertices[i] == vOpposite) {
				return hNeighbors[i];
			}
		}
		return null;
	}

	// sets back-and-forth hull neighbor pointers between this side and
	// hNeighbor over the given shared DIM-2-simplex
	public void hLinkTo(Side hNeighbor, int sideIdx) {
		hNeighbors[sideIdx] = hNeighbor;
		int vNeighborOppositeIdx = hNeighbor.vertexOppositeIndex(this, vertices[sideIdx]);
		hNeighbor.hNeighbors[vNeighborOppositeIdx] = this;
	}

	public boolean facesVertex(double[] q) {
		// www.cs.cmu.edu/~quake/robust.html
		double[] matrix = new double[DIM * DIM];
		for (int row = 0; row < DIM; row++) {
			double[] vals = vertices[row];
			for (int col = 0; col < DIM; col++) {
				matrix[row * DIM + col] = vals[col] - q[col];
			}
		}
		return isReverse ^ (det(matrix, DIM) > 0.0);
	}

	public boolean hasVertex(double[] q) {
		for (double[] v : vertices) {
			if (v == q) {
				return true;
			}
		}
		return false;
	}

	// finds this side's vertex opposite a given DIM-2-simplex (given as the
	// DIM-2 simplex of side opposite pOppositeNeighbor)
	public double[] vertexOpposite(Side sideNeighbor, double[] vOppositeNeighbor) {
		return vertices[vertexOppositeIndex(sideNeighbor, vOppositeNeighbor)];
	}

	public int vertexOppositeIndex(Side sideNeighbor, double[] vOppositeNeighbor) {
		for (int vIdx = 0; vIdx < DIM; vIdx++) {
			double[] v = vertices[vIdx];
			if (v == vOppositeNeighbor || !sideNeighbor.hasVertex(v)) {
				// first condition is fallback for when this.r === sideNeighbor
				return vIdx;
			}
		}
		return -1;
	}

	// vals contains the rows of the (dim x dim) matrix concatenated
	public static double det(double[] vals, int size) {
		if (size == 4) {
			return vals[0]
					* (vals[5] * (vals[10] * vals[15] - vals[11] * vals[14])
							- vals[6] * (vals[9] * vals[15] - vals[11] * vals[13])
							+ vals[7] * (vals[9] * vals[14] - vals[10] * vals[13]))
					- vals[1] * (vals[4] * (vals[10] * vals[15] - vals[11] * vals[14])
							- vals[6] * (vals[8] * vals[15] - vals[11] * vals[12])
							+ vals[7] * (vals[8] * vals[14] - vals[10] * vals[12]))
					+ vals[2] * (vals[4] * (vals[9] * vals[15] - vals[11] * vals[13])
							- vals[5] * (vals[8] * vals[15] - vals[11] * vals[12])
							+ vals[7] * (vals[8] * vals[13] - vals[9] * vals[12]))
					- vals[3] * (vals[4] * (vals[9] * vals[14] - vals[10] * vals[13])
							- vals[5] * (vals[8] * vals[14] - vals[10] * vals[12])
							+ vals[6] * (vals[8] * vals[13] - vals[9] * vals[12]));
		} else if (size == 3) {
			return vals[0] * (vals[4] * vals[8] - vals[5] * vals[7]) - vals[1] * (vals[3] * vals[8] - vals[5] * vals[6])
					+ vals[2] * (vals[3] * vals[7] - vals[4] * vals[6]);
		} else if (size == 2) {
			return vals[0] * vals[3] - vals[1] * vals[2];
		} else if (size == 1) {
			return vals[0];
		} else if (size < 2) {
			return -1.0;
		} else {
			double sum = 0.0;
			int factor = 1;
			for (int col = 0; col < size; col++) {
				double[] subMatrix = new double[(size - 1) * (size - 1)];
				for (int row = 0; row < size - 1; row++) {
					// copy row, leaving out the current column
					int colIndex = 0;
					for (int col2 = 0; col2 < size; col2++) {
						if (col2 != col) {
							subMatrix[row * (size - 1) + colIndex] = vals[(row + 1) * size + col2];
							colIndex++;
						}
					}
				}
				sum += factor * vals[col] * det(subMatrix, size - 1);
				factor *= -1;
			}
			return sum;
		}
	}

}
