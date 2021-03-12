package com.forschungsprojekt.spring_backend.chFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

// Copyright: Felix Weitbrecht, Universität Stuttgart
public class ExampleCode {

	public static void main(String[] args) {
		// test data
		int dimensions = 2;
		int vertexCount = 100;
		ArrayList<double[]> exampleVertices = generateVertices(vertexCount, dimensions);
		long startTime = System.currentTimeMillis();

		System.out
				.println("Let's filter cost vectors with " + dimensions + " dimensions (using made-up cost vectors).");
		System.out.println("Create a CHFilter object...");
		CHFilter filter = new CHFilter(dimensions);

		System.out.println("Add some cost vectors...");
		double[] someVertex1 = exampleVertices.remove(0);
		double[] someVertex2 = exampleVertices.remove(0);
		filter.addVertex(someVertex1);
		filter.addVertex(someVertex2);

		System.out.println(
				"We can always query the CHFilter object for the cost vectors that are (potentially) optimal for some alpha:");
		HashSet<double[]> goodCosts = filter.getFilteredVertices();
		prettyPrint(goodCosts, dimensions);

		System.out.println(
				"We know whether the set of filtered cost vectors changed after adding a cost vector without having to compare the old set and new set:");
		boolean changed;
		double[] someVertex3 = exampleVertices.remove(0);
		double[] someVertex4 = exampleVertices.remove(0);
		changed = filter.addVertex(someVertex3);
		System.out.println("\tDid this cost vector cause a change? " + yesOrNo(changed));
		changed = filter.addVertex(someVertex4);
		System.out.println("\tDid this cost vector cause a change? " + yesOrNo(changed));

		System.out.println(
				"If we add two cost vectors with the same values, or almost the same values, the second one is ignored:");
		changed = filter.addVertex(someVertex4);
		System.out.println("\tDid this cost vector cause a change? " + yesOrNo(changed));

		System.out.println("Let's add the rest of the cost vectors:");
		for (double[] v : exampleVertices) {
			filter.addVertex(v);
		}

		System.out.println("Again, we can query the cost vectors that are optimal for some alpha:");
		HashSet<double[]> goodCosts2 = filter.getFilteredVertices();
		prettyPrint(goodCosts2, dimensions);

		System.out.println(
				"Important note: the double[] arrays representing cost vectors must not have their values changed after being added into the filter!");

		long endTime = System.currentTimeMillis();
		System.out.println("Inserting " + vertexCount + " cost vectors in " + dimensions + " dimensions took "
				+ (endTime - startTime) + "ms.");
	}

	private static String yesOrNo(boolean b) {
		return b ? "Yes!" : "No.";
	}

	private static void prettyPrint(HashSet<double[]> vertices, int dimensions) {
		for (double[] v : vertices) {
			System.out.print("\t[");
			for (int d = 0; d < dimensions; d++) {
				System.out.print(String.format("%.3f", v[d]));
				if (d < dimensions - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("]");
		}
	}

	private static ArrayList<double[]> generateVertices(int count, int dim) {
		Random random = new Random(42L);
		int canvasSize = 10;
		ArrayList<double[]> vertices = new ArrayList<double[]>(count);
		for (int i = 0; i < count; i++) {
			double[] vals = new double[dim];
			for (int dimIdx = 0; dimIdx < dim; dimIdx++) {
				vals[dimIdx] = canvasSize * random.nextDouble();
			}
			vertices.add(vals);
		}
		return vertices;
	}

}
