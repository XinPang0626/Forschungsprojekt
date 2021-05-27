package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Testmethods {

        public static void printtxt(String name, List<String> result){
                System.out.println("x");
                try {
          
                        FileWriter myWriter = new FileWriter("/scratch/altprp/results/"+name+".txt");
            
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

        
    


    public static void trialsAstar(Graph g, List<String> result, String name) {
        double[] alpha = new double[g.getNrOFMetrik()];
         Arrays.fill(alpha, 0.5);
  
       

        long preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        long preprocesstimeEnd = System.nanoTime();
        long difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for one landmark:" + difftime);
        printtxt(name +"1", result);

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for five landmark:" + difftime);
        printtxt(name +"2", result);

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithFiveLandmark = new AStar_Standard(g, "ALT", 5);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for ten landmark: " + difftime);
        printtxt(name +"3", result);

        

        aStarWithOneLandmark.setAlpha(alpha);
        aStarWithTwoLandmark.setAlpha(alpha);
        aStarWithFiveLandmark.setAlpha(alpha);
        
        long totalTimeAStarWithOneLandmark = 0;
        long totalTimeAStarWithTwoLandmark = 0;
        long totalTimeAStarWithFiveLandmark = 0;
       
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

            // astar with 2 landmark
            sTime = System.nanoTime();
            aStarWithTwoLandmark.compute();
            aStarWithTwoLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithTwoLandmark += time;

            // astar with 5 landmark
            sTime = System.nanoTime();
            aStarWithFiveLandmark.compute();
            aStarWithFiveLandmark.getShortestPathInLonLat(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithFiveLandmark += time;

        
        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / nrOfTrial;
        long averageTimeAStarWithFiveLandmark = totalTimeAStarWithFiveLandmark / nrOfTrial;
       
        String landOne = "aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds";
        String landTwo = "aStar with ALT and two landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds";
        String landFive = "aStar with ALT and five landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithFiveLandmark + "] nano seconds";
      
        System.out.println(landOne);
        System.out.println(landTwo);
        System.out.println(landFive);
       
        result.add(landOne);
        result.add(landTwo);
        result.add(landFive);
        printtxt(name +"4", result);

        
        int start = 123;
        int target = 5423;
        
     
        aStarWithOneLandmark.setStart(start);
        aStarWithOneLandmark.setTarget(target);
        aStarWithOneLandmark.setAlpha(alpha);

        long totalTimeAStar = 0;
        long totalTimedij = 0;
        long totalpathdifference=0;

        result.add("PATH DIFFERENCE BETWEEN ASTAR AND DIJ");
        
        for (int i = 0; i < nrOfTrial; i++) {
            start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            target = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target

            // a*
            long sTime = System.nanoTime();
            aStarWithOneLandmark.compute();
            int [] astarpath= aStarWithOneLandmark.getShortestPathTo(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStar += time;

            // dijkstra
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha);
           int[] dijpath= d.getShortestPathTo(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimedij += time;
            int pathdifference= astarpath.length- dijpath.length;
            totalpathdifference+=pathdifference;
            result.add("Astarpath length: "+ astarpath.length+" Dijpath length: "+ dijpath.length + "-difference: "+pathdifference);
            
        }
        long averageTimeAStar = totalTimeAStar / nrOfTrial;
        long averageTimedij = totalTimedij / nrOfTrial;
        long averagePathdiff= totalpathdifference/ nrOfTrial;
        String averagepath= "Average difference between astar and Dij";
        String altaverage = "aStar with ALT Computation and path retrieval took in average [" + averageTimeAStar
                + "] nano seconds";
        String dijaverage = "Dijkstra Computation and path retrieval took in average [" + averageTimedij
                + "] nano seconds";
        System.out.println(altaverage);
        System.out.println(dijaverage);
        System.out.println(averagepath);
        result.add(averagepath);
        result.add(altaverage);
        result.add(dijaverage);
        printtxt(name +"5", result);

       
    }

    
}
