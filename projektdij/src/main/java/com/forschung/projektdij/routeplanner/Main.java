package com.forschung.projektdij.routeplanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
	static Graph graph;

	/**
	 * Interface to call up routeplanning
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter path to File: ");
		String path = scanner.nextLine();
		graph = new Graph(path);
		System.out.println("Enter the personal cost coefficient alpha(2 positive number seperate with one space): ");
		String parameter = scanner.nextLine();
		String[] tempStringArray = parameter.split(" ");
		double[] alpha = new double[2];
		alpha[0] = Double.parseDouble(tempStringArray[0]);
		alpha[1] = Double.parseDouble(tempStringArray[1]);

		while (true) {
			System.out.println("choose function:");
			System.out.println("---------------------------------------");
			System.out.println("| 1: Dijkstra with start-target query |");
			System.out.println("|-------------------------------------|");
			System.out.println("| 2: Dijkstra with start and query    |");
			System.out.println("|-------------------------------------|");
			System.out.println("| 3: File                             |");
			System.out.println("|-------------------------------------|");
			System.out.println("| 4: End                              |");
			System.out.println("---------------------------------------");
			String function = scanner.next();

			if (function.equals("1") || function.equals("2") || function.equals("3")) {
				if (function.equals("1")) {
					System.out.println("source: ");
					int source = scanner.nextInt();
					System.out.println("target: ");
					int target = scanner.nextInt();
					Dijkstra dijk = new Dijkstra(graph, source, alpha);
					System.out.println("shortest path between " + source + " and " + target + ": "
							+ dijk.getCostOfShortestPathTo(target));

				} else if (function.equals("2")) {
					System.out.println("start:\t");
					int start = scanner.nextInt();
					Dijkstra dijk = new Dijkstra(graph, start, alpha);
					while (true) {
						System.out.println("Do you want to query? y/n  ");
						String answer = scanner.next();
						if (answer.equals("n")) {
							break;
						} else if (answer.equals("y")) {
							System.out.println("target: ");
							int target = scanner.nextInt();
							System.out.println("the cost of the shortest path between " + start + " and " + target
									+ ": " + dijk.getCostOfShortestPathTo(target));
						}
					}

				} else if (function.equals("3")) {
					System.out.println("Enter file path: ");
					String path2 = scanner.next();
					BufferedReader br = null;
					FileWriter fileWriter = null;
					PrintWriter printWriter = null;
					String line = null;
					String[] split;
					int acutalStart = -1;
					Dijkstra dijk = null;
					
					try {
						br = new BufferedReader(new FileReader(path2));
						fileWriter = new FileWriter("result.txt"); // create file
						printWriter = new PrintWriter(fileWriter);

						while ((line = br.readLine()) != null) {
							split = line.split(" ");
							int start = Integer.parseInt(split[0]);
							int target = Integer.parseInt(split[1]);

							if (acutalStart != start) {
								dijk = new Dijkstra(graph, start, alpha);
							}
							printWriter.println(dijk.getCostOfShortestPathTo(target));// prints calculation in file
							System.out.println(dijk.getCostOfShortestPathTo(target));
							acutalStart = start;
						}
						printWriter.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (function.equals("4")) { // end program
				System.out.println("successfully ended");
				scanner.close();
				break;
			}
		}

	}

}
