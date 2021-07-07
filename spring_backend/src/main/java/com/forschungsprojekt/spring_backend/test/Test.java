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

        //Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/graph-files/map.txt");//load large graph (bawu)
        Graph g = new Graph("/scratch/altprp/germany_car_2.graph");
        /**
         * compare a* and dijkstra
         */

        int start = 123;
        int target = 5423;
        double[] alpha = {0.5, 0.5};
       
        
        long totalTimeAStar = 0;
        long totalTimedij = 0;
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); //choose a random start point
            target = (int) Math.random() * g.getNodeNr(); //choose a random target point
         
            //dijkstra
            long sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha);
		    d.getCostOfShortestPathTo(target);
		    long eTime = System.nanoTime();
		    long time = eTime - sTime;
            totalTimedij += time;
        }
        long averageTimeAStar = totalTimeAStar / nrOfTrial;
        long averageTimedij = totalTimedij  / nrOfTrial;
        System.out.println("Dijkstra Computation and path retrieval took in average ["+averageTimedij+"] nano seconds");
        /**
         * In average and 200 trials:
         * aStar with 1 landmark takes 377475 nano secs while dij takes 16606857 nano secs
         */



        




    }
}
