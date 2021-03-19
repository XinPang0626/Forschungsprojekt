package com.forschungsprojekt.spring_backend.geometry;

//Copyright: Felix Weitbrecht, Universität Stuttgart
public class Simplex extends AbstractSimplex {

	// convention: simplex has side 0 as base, with newest vertex as top vertex
	public Simplex(Side[] sides) {
		this.sides = sides;
		introduceSelfToSides();
	}

	// v must be vertex of this simplex
	public Side sideOpposite(double[] v) {
		for (Side side : sides) {
			if (!side.hasVertex(v)) {
				return side;
			}
		}
		return null;
	}

	public double[] topVertex() {
		for (double[] v : sides[1].vertices) {
			if (!sides[0].hasVertex(v)) {
				return v;
			}
		}
		return null;
	}
	
}
