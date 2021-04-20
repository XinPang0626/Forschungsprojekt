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
        Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/FP/graph-files/bremen.txt");//load small graph (bremen)
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
        
        long totalTimeAStar = 0;
        long totalTimedij = 0;
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); //choose a random start point
            target = (int) Math.random() * g.getNodeNr(); //choose a random target point
            aStar.setStart(start);// set the start
            aStar.setTarget(target);//set the target

            //a*
            long sTime = System.nanoTime();
		    aStar.compute();
            aStar.getShortestPathInLonLat(target);
		    long eTime = System.nanoTime();
		    long time = eTime - sTime;
            totalTimeAStar += time;

            //dijkstra
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha);
		    d.getCostOfShortestPathTo(target);
		    eTime = System.nanoTime();
		    time = eTime - sTime;
            totalTimedij += time;
        }
        long averageTimeAStar = totalTimeAStar / nrOfTrial;
        long averageTimedij = totalTimedij  / nrOfTrial;
        System.out.println("aStar with ALT Computation and path retrieval took in average ["+averageTimeAStar+"] nano seconds");
        System.out.println("Dijkstra Computation and path retrieval took in average ["+averageTimedij+"] nano seconds");
        /**
         * In average and 200 trials:
         * aStar with 1 landmark takes 377475 nano secs while dij takes 16606857 nano secs
         */



        /**
         * campare aStar with 1, 2 and 4 landmarks
         */
        AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        AStar_Standard aStarWithFourLandmark = new AStar_Standard(g, "ALT", 4);
        aStarWithOneLandmark.setAlpha(alpha);
        aStarWithTwoLandmark.setAlpha(alpha);
        aStarWithFourLandmark.setAlpha(alpha);
        long totalTimeAStarWithOneLandmark = 0;
        long totalTimeAStarWithTwoLandmark = 0;
        long totalTimeAStarWithFourLandmark = 0;
        nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); //choose a random start point
            target = (int) Math.random() * g.getNodeNr(); //choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);//set the target
            aStarWithTwoLandmark.setStart(start);// set the start
            aStarWithTwoLandmark.setTarget(target);//set the target
            aStarWithFourLandmark.setStart(start);// set the start
            aStarWithFourLandmark.setTarget(target);//set the target

            //astar with 1 landmark
            long sTime = System.nanoTime();
		    aStarWithOneLandmark.compute();
            aStarWithOneLandmark.getShortestPathInLonLat(target);
		    long eTime = System.nanoTime();
		    long time = eTime - sTime;
            totalTimeAStarWithOneLandmark += time;

            //astar with 2 landmark
            sTime = System.nanoTime();
		    aStarWithTwoLandmark.compute();
            aStarWithTwoLandmark.getShortestPathInLonLat(target);
		    eTime = System.nanoTime();
		    time = eTime - sTime;
            totalTimeAStarWithTwoLandmark += time;

            //astar with 4 landmark
            sTime = System.nanoTime();
            aStarWithFourLandmark.compute();
            aStarWithFourLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithFourLandmark += time;
        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / nrOfTrial;
        long averageTimeAStarWithFourLandmark = totalTimeAStarWithFourLandmark / nrOfTrial;
        System.out.println("aStar with ALT and one landmark Computation and path retrieval took in average ["+averageTimeAStarWithOneLandmark+"] nano seconds");
        System.out.println("aStar with ALT and two landmark Computation and path retrieval took in average ["+averageTimeAStarWithTwoLandmark+"] nano seconds");
        System.out.println("aStar with ALT and four landmark Computation and path retrieval took in average ["+averageTimeAStarWithFourLandmark+"] nano seconds");
        /**
         * In average with 200 trials:
         * 1 landmark: 341318 nano secs
         * 2 landmarks: 568448 nano secs
         * 4 landmarks: 344786 nano secs
         */
        




    }
}
