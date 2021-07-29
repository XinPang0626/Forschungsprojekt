package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Testmethods {

        public static void printtxt(String name, List<String> result){
                System.out.println("x");
                try {
                                           // "/scratch/altprp/results/"+name+".txt"
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

        
    


    public static void trialsAstar(Graph g, List<String> result, String name) {
        double[] alpha = {0.5,0.5};
      
  
       

        long preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        long preprocesstimeEnd = System.nanoTime();
        long difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for one landmark:" + difftime);
       computepath(aStarWithOneLandmark, 1, result, g);
        printtxt(name +"onelandmark", result);

        /** 
        
        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for two landmark:" + difftime);
        computepath(aStarWithTwoLandmark, 2, result, g);
        printtxt(name +"twolandmark", result);
        

        preprocesstimestart = System.nanoTime();
        AStar_Standard aStarWithFiveLandmark = new AStar_Standard(g, "ALT", 5);
        preprocesstimeEnd = System.nanoTime();
        difftime = preprocesstimeEnd - preprocesstimestart;
        result.add("Preprocessing time for five landmark: " + difftime);
        computepath(aStarWithFiveLandmark, 5, result, g);
        printtxt(name +"fivelandmark", result);
*/
        

      

        
        int start = 123;
        int target = 5423;
        
     
        aStarWithOneLandmark.setStart(start);
        aStarWithOneLandmark.setTarget(target);
        aStarWithOneLandmark.setAlpha(alpha);

        long totalTimeAStar = 0;
        long totalTimedij = 0;
        long totalpathdifference=0;

        result.add("PATH DIFFERENCE BETWEEN ASTAR AND DIJ");
        
        for (int i = 0; i < 200; i++) {
            start = 112402; //(int) ((Math.random() * g.getNodeNr()));
            target = 108972; //(int) ((Math.random() * g.getNodeNr())); // choose a random target point
            System.out.println(start);
            System.out.println(target);
            aStarWithOneLandmark.setStart(start);// set the start
            aStarWithOneLandmark.setTarget(target);// set the target

            // a*
            long sTime = System.nanoTime();
            long nTime= System.nanoTime();
            aStarWithOneLandmark.compute();
            //int [] astarpath= aStarWithOneLandmark.getShortestPathTo(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            totalTimeAStar += time;

            // dijkstra
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha);
           //int[] dijpath= d.getShortestPathTo(target);
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimedij += time;
           // int pathdifference= astarpath.length- dijpath.length;
            //totalpathdifference+=pathdifference;
          
            
        }
        long averageTimeAStar = totalTimeAStar / 200;
        long averageTimedij = totalTimedij / 200;
        //long averagePathdiff= totalpathdifference/ 200;
        //String averagepath= "Average difference between astar and Dij: "+ averagePathdiff;
        String altaverage = "aStar with ALT Computation and path retrieval took in average [" + averageTimeAStar
                + "] nano seconds";
        String dijaverage = "Dijkstra Computation and path retrieval took in average [" + averageTimedij
                + "] nano seconds";
        System.out.println(altaverage);
        System.out.println(dijaverage);
        //System.out.println(averagepath);
        //result.add(averagepath);
        result.add(altaverage);
        result.add(dijaverage);
        printtxt(name +"3", result);

       
    }

   public static void computepath(AStar_Standard astar, int numberoflandmarks, List<String> result, Graph g){
        double[] alpha = {0.5,0.5};
        astar.setAlpha(alpha);

        long totalTimeAStarWithOneLandmark = 0;
        long totalwithoutpathretrieval =0;
        int nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {
            int start = (int) ((Math.random() * g.getNodeNr())); // choose a random start point
            int target = (int) ((Math.random() * g.getNodeNr())); // choose a random target point
            System.out.println(start);
            System.out.println(target);
           
           
            astar.setStart(start);// set the start
            astar.setTarget(target);// set the target
           
            // astar with 1 landmark
            long sTime = System.nanoTime();
            long nTime= System.nanoTime();
            astar.compute();
            long enTime = System.nanoTime();
            
           // astar.getShortestPathInLonLat(target);
            long eTime = System.nanoTime();
            long time = eTime - sTime;
            long wtime= nTime-enTime;
            totalTimeAStarWithOneLandmark += time;
            totalwithoutpathretrieval +=wtime;
        
        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averagewithoutpath=totalwithoutpathretrieval/nrOfTrial;
        
       
        String landOne = "aStar with ALT and "+ numberoflandmarks +" landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds";
        String without= "aStar with ALT and "+ numberoflandmarks +" landmark Computation took in average ["
        + averagewithoutpath + "] nano seconds";
       
        System.out.println(landOne);
        System.out.println(without);
    
       
        result.add(landOne);
        result.add(without);
       




    }

    
}