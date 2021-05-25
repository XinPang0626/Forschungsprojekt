package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class SmallerTest {
    
    static List<String> result = new ArrayList<>();
    
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */


        Graph g5 = new Graph("/scratch/altprp/saarland_car_2.graph");
        Graph g6 = new Graph("/scratch/altprp/saarland_car_3.graph");
      

        result.add("---SAARLAND CAR 2 GRAPH---");
        comparisonASTARdij(g5);
        trialsAstar(g5);

        result.add("---SAARLAND CAR 3 GRAPH---");
        comparisonASTARdij(g6);
        trialsAstar(g6);

       
      

        try {

            FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/SMALLRESULT.txt");

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


    public static void trialsAstar(Graph g) {
        double[] alpha = new double[g.getNrOFMetrik()];
         Arrays.fill(alpha, 0.5);
  
       

        long preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        long preprocesstimeEnd = System.nanoTime();
        long difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for one landmark:" + difftime);

        

        aStarWithOneLandmark.setAlpha(alpha);
      
        long totalTimeAStarWithOneLandmark = 0;
       
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            int start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            int target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target
          

            // astar with 1 landmark
            long sTime = System.nanoTime();
            aStarWithOneLandmark.compute();
            aStarWithOneLandmark.getShortestPathInLonLat(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStarWithOneLandmark += time;

          

        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        
        String landOne = "aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds";
        
        System.out.println(landOne);
        
        result.add(landOne);
        
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