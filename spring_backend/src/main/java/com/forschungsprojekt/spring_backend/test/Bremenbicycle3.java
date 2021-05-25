package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Bremenbicycle3 {
    public static List<String> result =new ArrayList<>();;
    public static void main(String[] args) {
        result.add("BREMEN bicycle 3---");
        Graph g5 = new Graph("/scratch/altprp/bremen_bicycle_3.graph");
        trialsAstar(g5);

    }


    public static void printtxt(String name){
        try {

                FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/"+name+".txt");
    
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
        printtxt("bremenlandbicy_1");

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for two landmark:" + difftime);
        printtxt("bremenlandbicy_2");

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithFiveLandmark = new AStar_Standard(g, "ALT", 5);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for five landmark: " + difftime);
        printtxt("bremenlandbicy_3");

       

        aStarWithOneLandmark.setAlpha(alpha);
        aStarWithTwoLandmark.setAlpha(alpha);
        aStarWithFiveLandmark.setAlpha(alpha);
      
        long totalTimeAStarWithOneLandmark = 0;
        long totalTimeAStarWithTwoLandmark = 0;
        long totalTimeAStarWithTenLandmark = 0;
       
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            int start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            int target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target
            aStarWithTwoLandmark.setStart(start);// set the start
            aStarWithTwoLandmark.setTarget(target);// set the target
            aStarWithFiveLandmark.setStart(start);// set the start
            aStarWithFiveLandmark.setTarget(target);// set the target
            

            // astar with 1 landmark
            long sTime = System.nanoTime();
            aStarWithOneLandmark.compute();
            aStarWithOneLandmark.getShortestPathInLonLat(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStarWithOneLandmark += time;

            // astar with 5 landmark
            sTime = System.nanoTime();
            aStarWithTwoLandmark.compute();
            aStarWithTwoLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithTwoLandmark += time;

          

        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / nrOfTrial;
        long averageTimeAStarWithFiveLandmark = totalTimeAStarWithTenLandmark / nrOfTrial;
       
        String landOne = "aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds";
        String landTwo = "aStar with ALT and two landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds";
        String landTen = "aStar with ALT and five landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithFiveLandmark + "] nano seconds";
        
        System.out.println(landOne);
        System.out.println(landTwo);
        System.out.println(landTen);
        
        result.add(landOne);
        result.add(landTwo);
        result.add(landTen);
        printtxt("bremenlandbicy_4");
       


        int start = 123;
        int target = 5423;
        
       
         Arrays.fill(alpha, 0.5);
         aStarWithOneLandmark.setStart(start);
         aStarWithOneLandmark.setTarget(target);
         aStarWithOneLandmark.setAlpha(alpha);

        long totalTimeAStar = 0;
        long totalTimedij = 0;
      
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target

            // a*
            long sTime = System.nanoTime();
            aStarWithOneLandmark.compute();
            aStarWithOneLandmark.getShortestPathInLonLat(target);
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
        printtxt("bremenlandbicy_5");
    }

    
}
