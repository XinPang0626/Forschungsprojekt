package com.forschungsprojekt.spring_backend.routerplaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.forschungsprojekt.spring_backend.geometry.ConvexHull;
import com.forschungsprojekt.spring_backend.geometry.Facet;

/*
 * Allows filtering vertices (cost vectors) to keep only those vertices that
 * have minimal cost for some alpha. If there are at least d non-dominated
 * vertices, the filtered vertices are those vertices that are vertices of
 * "relevant" facets of the CH (convex hull) - those that are visible from
 * (min(x_1), ..., min(x_d)). If there are less than d non-dominated
 * vertices, the filtered vertices are exactly the non-dominated ones. So
 * internally, a CH is only created once there are d non-dominated vertices.
 * Until then, non-dominated vertices are maintained in a simple list.
 * 
 * Note that this is only a heuristic, so this might keep some unnecessary
 * vertices.
 * 
 * Note that the CH code (based on a Delaunay triangulation code) has been
 * adapted for this specific purpose. In particular, this means: The
 * computed triangulation is probably not Delaunay. Vertices may be dropped
 * if they are guaranteed to not be optimal for any alpha. This can happen
 * when a vertex is inside the CH or not near an existing relevant facet.
 */
//Copyright: Felix Weitbrecht, Universität Stuttgart
public class CHFilter {

	public static int DIM;

	// CH is shifted to be very close to the origin (leaves margin for error)
	private double[] vOriginShifted;
	private static final double originOffset = 1e-10;
	int originShiftVersion = 0; // for origin visbility test caching: how many
								// times the origin was shifted

	// a cached list of vertices which are optimal for some alpha
	private HashSet<double[]> filteredVertices = new HashSet<double[]>();

	// the first d non-dominated vertices
	private ArrayList<double[]> firstVertices = null;

	// the CH (with a triangulation inside)
	private ConvexHull ch = null;

	// for duplicate detection: managed vertices sorted by first component
	private TreeSet<double[]> managedVertices = new TreeSet<double[]>(vertexComp);
	private static final double eps = 1e-10;

	private static Comparator<double[]> vertexComp = new Comparator<double[]>() {
		@Override
		public int compare(double[] v1, double[] v2) {
			if (v1 == v2) {
				return 0;
			}
			int compared = Double.compare(v1[0], v2[0]);
			if (compared == 0) {
				return Integer.compare(v1.hashCode(), v2.hashCode());
			}
			return compared;
		}
	};

	public CHFilter(int dimension) {
		DIM = dimension;
		firstVertices = new ArrayList<double[]>(DIM);
	}

	// returns true if v is not filtered
	public boolean addVertex(double[] v) {

		if (Math.random() > 1.0 / 5) {
			computeFilteredVertices();
		}

		if (!isNew(v)) {
			return false;
		}

		boolean isHullVertex;
		// are we still gathering d non-dominated vertices or do we have a
		// proper CH to insert into already?
		if (ch == null) {
			isHullVertex = true;
			// remove all existing non-dominated vertices that v dominates
			for (int i = firstVertices.size() - 1; i >= 0; i--) {
				int dominationOrder = getDominationOrder(v, firstVertices.get(i));
				if (dominationOrder == -1) {
					// v is dominated
					isHullVertex = false;
					break;
				} else if (dominationOrder == 1) {
					// this vertex is dominated by v, remove it
					firstVertices.remove(i);
				}
			}
			if (isHullVertex) {
				// v is not dominated, keep it
				firstVertices.add(v);
				if (firstVertices.size() == DIM) {
					// we have enough vertices to create the CH now
					ch = new ConvexHull(firstVertices);

					// initialize shifted origin
					vOriginShifted = Arrays.copyOf(v, DIM);
					for (double[] v1 : firstVertices) {
						for (int d = 0; d < DIM; d++) {
							if (v1[d] < vOriginShifted[d]) {
								vOriginShifted[d] = v1[d];
							}
						}
					}
					for (int d = 0; d < DIM; d++) {
						vOriginShifted[d] -= originOffset;
					}
				}
			}
		} else {
			// insert into CH
			ArrayList<Facet> newFacets = ch.addVertex(v, vOriginShifted, originShiftVersion);
			updateOrigin(v);
			isHullVertex = false;
			if (newFacets != null) {
				for (Facet f : newFacets) {
					if (f.sides[0].isRelevant(vOriginShifted, originShiftVersion)) {
						isHullVertex = true;
						break;
					}
				}
			}
		}

		managedVertices.add(v);
		if (isHullVertex) {
			// invalidate cache
			filteredVertices.clear();
		}
		return isHullVertex;
	}

	private boolean isNew(double[] v) {
		// explore managed vertices from v's insertion position in
		// managedVertices in both directions for eps-equal vertices

		// use tailSet for >= vertices
		SortedSet<double[]> tailSet = managedVertices.tailSet(v, true);
		Iterator<double[]> iter = tailSet.iterator();
		while (iter.hasNext()) {
			double[] next = iter.next();
			if (next[0] > v[0] + eps) {
				break;
			}
			boolean isDupe = true;
			for (int d = 1; d < DIM; d++) {
				if (Math.abs(next[d] - v[d]) > eps) {
					isDupe = false;
					break;
				}
			}
			if (isDupe) {
				return false;
			}
		}

		// use lower() for < vertices
		double[] next = managedVertices.lower(v);
		while (next != null) {
			if (next[0] < v[0] - eps) {
				break;
			}
			boolean isDupe = true;
			for (int d = 1; d < DIM; d++) {
				if (Math.abs(next[d] - v[d]) > eps) {
					isDupe = false;
					break;
				}
			}
			if (isDupe) {
				return false;
			}
		}

		// no dupe found? return true
		return true;
	}

	// after adding v, update the shifted origin if necessary
	private void updateOrigin(double[] v) {
		boolean updated = false;
		for (int d = 0; d < DIM; d++) {
			if (v[d] - originOffset < vOriginShifted[d]) {
				vOriginShifted[d] = v[d] - originOffset;
				updated = true;
			}
		}
		if (updated) {
			originShiftVersion++;
		}
	}

	public HashSet<double[]> getFilteredVertices() {
		if (filteredVertices.size() == 0) {
			// cache invalidated
			computeFilteredVertices();
		}
		return filteredVertices;
	}

	private void computeFilteredVertices() {
		// update the list of filtered vertices, and, if the list of managed
		// vertices is too large, rebuild it
		if (ch == null) {
			filteredVertices.addAll(firstVertices);
			// if managedVertices has too many removed vertices, update it
			if (managedVertices.size() > DIM * 8) {
				managedVertices.clear();
				managedVertices.addAll(firstVertices);
			}
		} else {
			HashSet<double[]> allVertices = new HashSet<double[]>();
			for (Facet f : ch.livingFacets) {
				boolean relevant = f.sides[0].isRelevant(vOriginShifted, originShiftVersion);
				for (double[] v : f.sides[0].vertices) {
					allVertices.add(v);
					if (relevant) {
						filteredVertices.add(v);
					}
				}
			}
			// if managedVertices has too many removed vertices, update it
			if (managedVertices.size() > 3 * allVertices.size()) {
				managedVertices.clear();
				managedVertices.addAll(allVertices);
			}
		}
	}

	// returns 1 if v1 dominates v2, -1 if v2 dominates v1, and 0 otherwise
	private int getDominationOrder(double[] v1, double[] v2) {
		boolean allGeq0 = true;
		boolean allLeq0 = true;
		for (int d = 0; d < DIM; d++) {
			double diff = v1[d] - v2[d];
			if (diff < 0.0) {
				allGeq0 = false;
				if (!allLeq0) {
					return 0;
				}
			} else if (diff > 0.0) {
				allLeq0 = false;
				if (!allGeq0) {
					return 0;
				}
			}
		}
		if (allLeq0) {
			return 1;
		}
		return -1;
	}

}
