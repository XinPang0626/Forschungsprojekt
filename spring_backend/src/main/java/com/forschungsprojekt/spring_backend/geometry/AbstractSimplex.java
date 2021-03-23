package com.forschungsprojekt.spring_backend.geometry;

//Copyright: Felix Weitbrecht, Universität Stuttgart
public abstract class AbstractSimplex {

	// this simplex' bounding sides
	public Side[] sides;

	// whether this simplex has been destroyed yet
	public boolean isAlive = true;

	protected void introduceSelfToSides() {
		for (Side side : sides) {
			side.simplex = this;
		}
	}

}
