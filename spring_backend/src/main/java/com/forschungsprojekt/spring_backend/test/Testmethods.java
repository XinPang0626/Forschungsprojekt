package com.forschungsprojekt.spring_backend.test;

import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Testmethods {
    


    public static void trialsAstar(Graph g, List<String> result) {
        double[] alpha = new double[g.getNrOFMetrik()];
         Arrays.fill(alpha, 0.5);
  
       

        long preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        long preprocesstimeEnd = System.nanoTime();
        long difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for one landmark:" + difftime);

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithFiveLandmark = new AStar_Standard(g, "ALT", 5);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for five landmark:" + difftime);

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithTenLandmark = new AStar_Standard(g, "ALT", 10);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for ten landmark: " + difftime);

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithTwentyLandmark = new AStar_Standard(g, "ALT", 20);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for twenty landmark: " + difftime);

        aStarWithOneLandmark.setAlpha(alpha);
        aStarWithFiveLandmark.setAlpha(alpha);
        aStarWithTenLandmark.setAlpha(alpha);
        aStarWithTwentyLandmark.setAlpha(alpha);
        long totalTimeAStarWithOneLandmark = 0;
        long totalTimeAStarWithFiveLandmark = 0;
        long totalTimeAStarWithTenLandmark = 0;
        long totalTimeAStarWithTwentyLandmark = 0;
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            int start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            int target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target
            aStarWithFiveLandmark.setStart(start);// set the start
            aStarWithFiveLandmark.setTarget(target);// set the target
            aStarWithTenLandmark.setStart(start);// set the start
            aStarWithTenLandmark.setTarget(target);// set the target
            aStarWithTwentyLandmark.setStart(start);// set the start
            aStarWithTwentyLandmark.setTarget(target);// set the target

            // astar with 1 landmark
            long sTime = System.nanoTime();
            aStarWithOneLandmark.compute();
            aStarWithOneLandmark.getShortestPathInLonLat(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStarWithOneLandmark += time;

            // astar with 5 landmark
            sTime = System.nanoTime();
            aStarWithFiveLandmark.compute();
            aStarWithFiveLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithFiveLandmark += time;

            // astar with 10 landmark
            sTime = System.nanoTime();
            aStarWithTenLandmark.compute();
            aStarWithTenLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithTenLandmark += time;

            // astar with 20 landmark
            sTime = System.nanoTime();
            aStarWithTwentyLandmark.compute();
            aStarWithTwentyLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithTwentyLandmark += time;
        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithFiveLandmark / nrOfTrial;
        long averageTimeAStarWithFourLandmark = totalTimeAStarWithTenLandmark / nrOfTrial;
        long averageTimeAStarWithTwentyLandmark = totalTimeAStarWithTwentyLandmark / nrOfTrial;
        String landOne = "aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds";
        String landFive = "aStar with ALT and five landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds";
        String landTen = "aStar with ALT and ten landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithFourLandmark + "] nano seconds";
        String landTwenty = "aStar with ALT and twenty landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwentyLandmark + "] nano seconds";
        System.out.println(landOne);
        System.out.println(landFive);
        System.out.println(landTen);
        System.out.println(landTwenty);
        result.add(landOne);
        result.add(landFive);
        result.add(landTen);
        result.add(landTwenty);
    }

    static void comparisonASTARdij(Graph g, List<String> result) {
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
        /**
         * compare a* and dijkstra
         */

        int start = 123;
        int target = 5423;
        
        double[] alpha = new double[g.getNrOFMetrik()];
         Arrays.fill(alpha, 0.5);
        aStar.setStart(start);
        aStar.setTarget(target);
        aStar.setAlpha(alpha);

        long totalTimeAStar = 0;
        long totalTimedij = 0;
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStar.setStart(start);// set the start
            aStar.setTarget(target);// set the target

            // a*
            long sTime = System.nanoTime();
            aStar.compute();
            aStar.getShortestPathInLonLat(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStar += time;

            // dijkstra
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha);
            d.getCostOfShortestPathTo(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimedij += time;
        }
        long averageTimeAStar = totalTimeAStar / nrOfTrial;
        long averageTimedij = totalTimedij / nrOfTrial;
        String altaverage = "aStar with ALT Computation and path retrieval took in average [" + averageTimeAStar
                + "] nano seconds";
        String dijaverage = "Dijkstra Computation and path retrieval took in average [" + averageTimedij
                + "] nano seconds";
        System.out.println(altaverage);
        System.out.println(dijaverage);
        result.add(altaverage);
        result.add(dijaverage);

    }
}