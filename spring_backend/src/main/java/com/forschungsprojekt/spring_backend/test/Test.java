package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Test {
    static List<String> result = new ArrayList<>();

    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */
       int size=5;//extra variable which be added to constructor
        int[] zero = new int[size];
         Arrays.fill(zero, 0);
      
        for(int i=0; i<zero.length; i++){
          System.out.println("x "+zero[i]);  
        }
       
         

        Graph g1 = new Graph("/scratch/altprp/bawu_bicycle_3.graph");
        Graph g2 = new Graph("/scratch/altprp/bawu_car_3.graph");
        Graph g3 = new Graph("/scratch/altprp/bw_car_2.graph");
        Graph g4 = new Graph("/scratch/altprp/germany_car_2.graph");

        Graph g5 = new Graph("/scratch/altprp/saarland_car_2.graph");
        Graph g6 = new Graph("/scratch/altprp/saarland_car_3.graph");
        Graph g7 = new Graph("/scratch/altprp/saarland_bicycle_3.graph");

        result.add("---BAWU BICYCLE 3 GRAPH---");
        comparisonASTARdij(g1);
        trialsAstar(g1, 3);

        result.add("---BAWU CAR 3 GRAPH---");
        comparisonASTARdij(g2);
        trialsAstar(g2, 3);

        result.add("---BW CAR 2 GRAPH---");
        comparisonASTARdij(g3);
        trialsAstar(g3, 2);

        result.add("---GERMANY CAR 2 GRAPH---");
        comparisonASTARdij(g4);
        trialsAstar(g4, 2);

        result.add("---SAARLAND CAR 2 GRAPH---");
        comparisonASTARdij(g5);
        trialsAstar(g5, 2);

        result.add("---SAARLAND CAR 3 GRAPH---");
        comparisonASTARdij(g6);
        trialsAstar(g6, 3);

        result.add("---SAARLAND BICYCLE 3 GRAPH---");
        comparisonASTARdij(g7);
        trialsAstar(g7, 3);
      

        try {

            FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/RESULT.txt");

            for (int i = 0; i < result.size(); i++) {
                myWriter.write(result.get(i) + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }  

    }

    public static void trialsAstar(Graph g, int dim) {
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

    static void comparisonASTARdij(Graph g) {
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
