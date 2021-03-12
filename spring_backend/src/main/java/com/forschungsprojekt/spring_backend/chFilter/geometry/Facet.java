package com.forschungsprojekt.spring_backend.chFilter.geometry;

import java.util.ArrayList;

//Copyright: Felix Weitbrecht, Universität Stuttgart
public class Facet extends AbstractSimplex {

	// flag for search algorithms
	private boolean marked = false;

	private static ArrayList<Facet> markedFacets = new ArrayList<Facet>();

	public Facet(Side side) {
		this.sides = new Side[] { side };
		introduceSelfToSides();
	}

	public void mark() {
		marked = true;
		markedFacets.add(this);
	}

	public boolean isMarked() {
		return marked;
	}

	static public void unmarkAll() {
		for (Facet f: markedFacets) {
			f.marked = false;
		}
		markedFacets.clear();
	}

	@Override
	public String toString() {
		return "(" + sides[0].toString() + ")";
	}
	
}
