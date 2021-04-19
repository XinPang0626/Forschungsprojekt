package com.forschungsprojekt.spring_backend.test;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Test {
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation.
         * compute and print the time for precalculation.
         */
        Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/graph-files/map.txt");
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);

        /**
         * compare a* and dijkstra
         */

        int start = 123;
        int target = 5423;
        double[] alpha = {0.5, 0.5};
        aStar.setStart(start);
		aStar.setTarget(target);
		aStar.setAlpha(alpha);
        Dijkstra d = new Dijkstra(g, start, alpha);

        long sTime = System.currentTimeMillis();
		aStar.compute();
		long eTime = System.currentTimeMillis();
		long time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");

        sTime = System.currentTimeMillis();
		d.getCostOfShortestPathTo(target);
		eTime = System.currentTimeMillis();
		time = eTime - sTime;
		System.out.println("aStar with ALT Computation took ["+time+"] milli seconds");



    }
}
