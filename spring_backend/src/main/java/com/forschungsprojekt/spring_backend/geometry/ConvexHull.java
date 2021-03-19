package com.forschungsprojekt.spring_backend.geometry;

import static com.forschungsprojekt.spring_backend.routerplaner.CHFilter.DIM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

//Copyright: Felix Weitbrecht, Universität Stuttgart
// note that this is NOT a generic CH construction algorithm, as detailed in CHFilter.java
public class ConvexHull {

	public HashSet<Facet> livingFacets = new HashSet<Facet>();

	// given the first DIM vertices, construct the initial CH (two facets)
	public ConvexHull(ArrayList<double[]> firstVertices) {
		Side sideNew = new Side(firstVertices.toArray(new double[DIM][]));
		livingFacets.add(new Facet(sideNew));
		livingFacets.add(new Facet(sideNew.r));
		for (int sideIdx = 0; sideIdx < DIM; sideIdx++) {
			sideNew.hNeighbors[sideIdx] = sideNew.r;
			sideNew.r.hNeighbors[sideIdx] = sideNew;
		}
	}

	// returns the new facets (or null)
	public ArrayList<Facet> addVertex(double[] vNew, double[] vOrigin, int originVersion) {
		Facet someVisibleFacet = locate(vNew);
		if (someVisibleFacet == null) {
			// vertex inside CH, discard
			return null;
		} else {
			ArrayList<Facet> visibleFacets = findVisibleFacets(vNew, someVisibleFacet);

			// are any of the visible facets relevant?
			boolean keepVertex = false;
			for (Facet f : visibleFacets) {
				if (f.sides[0].isRelevant(vOrigin, originVersion)) {
					keepVertex = true;
					break;
				}
			}
			if (!keepVertex) {
				// how about their neighboring facets? (logic behind this
				// heuristic: if the new vertex produces a new relevant facet,
				// it must be visible to an existing relevant facet, or visible
				// to an existing facet with a neighboring relevant facet
				for (Facet f : visibleFacets) {
					for (Side sideNeighbor : f.sides[0].hNeighbors) {
						if (sideNeighbor.isRelevant(vOrigin, originVersion)) {
							keepVertex = true;
							break;
						}
					}
				}
			}

			if (keepVertex) {
				// update facet set and facets
				livingFacets.removeAll(visibleFacets);
				for (Facet f : visibleFacets) {
					f.isAlive = false;
				}

				// finally insert vertex into CH
				ArrayList<Facet> newFacets = createNewSimplices(visibleFacets, vNew);
				livingFacets.addAll(newFacets);
				return newFacets;
			} else {
				return null;
			}
		}
	}

	// exhaustive DFS
	private ArrayList<Facet> findVisibleFacets(double[] vNew, Facet someVisibleFacet) {
		ArrayList<Facet> visibleFacets = new ArrayList<Facet>();
		Stack<Facet> facetsToExplore = new Stack<Facet>();
		facetsToExplore.add(someVisibleFacet);
		while (!facetsToExplore.isEmpty()) {
			Facet facet = facetsToExplore.pop();
			if (!facet.isMarked() && facet.sides[0].facesVertex(vNew)) {
				facet.mark();
				visibleFacets.add(facet);
				for (Side side : facet.sides[0].hNeighbors) {
					facetsToExplore.add((Facet) side.simplex);
				}
			}
		}
		Facet.unmarkAll();
		return visibleFacets;
	}

	// bruteforce over all facets of CH
	private Facet locate(double[] vNew) {
		for (Facet f : livingFacets) {
			if (f.sides[0].facesVertex(vNew)) {
				return f;
			}
		}
		return null;
	}

	private ArrayList<Facet> createNewSimplices(ArrayList<Facet> visibleFacets, double[] vNew) {
		ArrayList<Facet> newFacets = new ArrayList<Facet>();
		for (Facet f : visibleFacets) {
			Side sideBase = f.sides[0];
			// find or create the DIM sides of the new simplex
			Side[] sides = new Side[DIM + 1];
			sides[0] = sideBase;
			double[][] sideBaseVertices = sideBase.vertices;
			for (int vIdx = 0; vIdx < DIM; vIdx++) {
				double[] vOpposite = sideBaseVertices[vIdx];
				Side sideNeighboringBase = sideBase.hNeighborOpposite(sideBaseVertices[vIdx]);
				if (!sideNeighboringBase.simplex.isAlive || sideNeighboringBase.simplex instanceof Simplex) {
					// side is shared with neighboring new simplex
					Simplex simplexNeighboring = sideNeighboringBase.simplex();
					if (simplexNeighboring != null) {
						sides[vIdx + 1] = simplexNeighboring.sides[1
								+ sideNeighboringBase.vertexOppositeIndex(sideBase, vOpposite)].r;
					} else {
						sides[vIdx + 1] = sideBase.createSideFacing(vNew, vIdx);
					}
				} else {
					// side is shared with new open simplex
					Side sideNew = sideBase.createSideFacing(vNew, vIdx);
					sides[vIdx + 1] = sideNew;
					newFacets.add(new Facet(sideNew.r));
					// set hull link
					sideNew.r.hLinkTo(sideBase.hNeighborOpposite(vOpposite), vIdx);
				}
			}
			new Simplex(sides);
		}

		// set hull links on new facets by rotating through new simplices
		// around new outside DIM-2-simplices to find hull neighbor
		for (Facet facet : newFacets) {
			Side side = facet.sides[0];
			double[][] sideVertices = side.vertices;
			for (int vIdx = 0; vIdx < DIM; vIdx++) {
				// link opposite vNew already set above
				if (side.hNeighbors[vIdx] == null) {
					double[] vOpposite = sideVertices[vIdx];
					Side sideHullNeighbor = rotateThroughSimplices(side, vOpposite).r;
					side.hLinkTo(sideHullNeighbor, vIdx);
				}
			}
		}

		return newFacets;
	}

	// rotates around the DIM-2-simplex opposite the specified vertex behind
	// sideFirst while simplices are found
	public static Side rotateThroughSimplices(Side sideFirst, double[] vOpposite) {
		while (true) {
			// rotate one step around the DIM-2-simplex, if possible
			Simplex simplexNext = sideFirst.r.simplex();
			if (simplexNext == null) {
				return sideFirst;
			}
			Side sideNext = simplexNext.sideOpposite(vOpposite);
			vOpposite = sideNext.vertexOpposite(sideFirst, vOpposite);
			sideFirst = sideNext;
		}
	}

}
