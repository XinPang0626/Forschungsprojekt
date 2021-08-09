package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Testsuit {

    static List<String> result = new ArrayList<>();

    public static void printtxt(String name, List<String> result) {
        System.out.println("x");
        try {
            // "./spring_backend/src/main/resources/"+name+".txt"
            FileWriter myWriter = new FileWriter("/scratch/altprp/results/" + name + ".txt");

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

    public static void main(String[] args) {

        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

        // Graph g = new Graph("/Users/xinpang/Desktop/Studium/5.
        // Semester/Forschungsprojekt/graph-files/stuttgart-travel-time-distance.txt");//load
        // large graph (bawu)
        Graph g = new Graph("/scratch/altprp/bremen.graph");

        /**
         * compare a* and dijkstra
         */

        long totalTimeAStar = 0;
        long totalTimedij = 0;
        int nrOfTrial = 200;
        int totalNrOfVisitedNodeAStar = 0;
        int totalNrOfVisitedNodeDij = 0;
        int samePath = 0;
        int sameCost = 0;

        boolean pathNotFound = true;
        int start = 0;
        int end = 0;
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
        double[] alpha = { 0.5, 0.5 };
        aStar.setAlpha(alpha);
        long sTime;
        long eTime;
        long time;
        long dtime = 0;
        long atime = 0;
        int nrOfVisitedNodeAStar = 0;
        int nrOfVisitedNodeDij = 0;
        for (int i = 0; i < nrOfTrial; i++) {
            System.out.println("Computing trial nr. " + i + " ...");
            // AStar:
            while (pathNotFound) {
                start = (int) (Math.random() * g.getNodeNr());// choose start and target randomly
                end = (int) (Math.random() * g.getNodeNr());
                aStar.setStart(start);
                aStar.setTarget(end);
                sTime = System.nanoTime();
                aStar.compute();
                eTime = System.nanoTime();
                if (aStar.getPathAvailable() == false) {
                    atime = eTime - sTime;
                    totalTimeAStar += atime;

                    nrOfVisitedNodeAStar += aStar.getNrOfVisitedNodes();
                    break;
                }
                aStar.reset();
            }

            // dijkstra:
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha, end);
            eTime = System.nanoTime();
            dtime = eTime - sTime;
            totalTimedij += dtime;

            nrOfVisitedNodeDij = d.getNrOfVisitedNodes();
            totalNrOfVisitedNodeDij += nrOfVisitedNodeDij;

            // compare cost and path:
            if (aStar.getCost(end) == d.getCost(end)) {
                sameCost++;
            }
            if (aStar.getShortestPathInLonLat().equals(d.getShortestPathInLonLat())) {
                samePath++;

            } else {
                System.out.println("start: " + start);
                System.out.println("target: " + end);
                System.out.println("cost aStar: " + aStar.getCost(end) + ", cost Dijkstra: " + d.getCost(end));
            }
            aStar.reset();
        }
        long averageTimeAStar = totalTimeAStar / samePath;
        long averageTimedij = totalTimedij / samePath;
        double averageNrOfVisitedNodesAStar = totalNrOfVisitedNodeAStar / samePath;
        double averageNrOfVisitedNodesDij = totalNrOfVisitedNodeDij / samePath;
        System.out.println("aStar with ALT Computation took in average [" + averageTimeAStar + "] nano seconds");
        // System.out.println("aStar with ALT visited in average [" +
        // averageNrOfVisitedNodesAStar +"] nodes");
        System.out.println("Dijkstra Computation took in average [" + averageTimedij + "] nano seconds");
        // System.out.println("Dijkstra visited in average [" +
        // averageNrOfVisitedNodesDij +"] nodes");
        System.out.println("The path is same in " + samePath + "/" + nrOfTrial);
        System.out.println("The cost is same in " + sameCost + "/" + nrOfTrial);
        result.add("aStar with ALT Computation took in average [" + averageTimeAStar + "] nano seconds");
        result.add("Dijkstra Computation took in average [" + averageTimedij + "] nano seconds");
        result.add("The path is same in " + samePath + "/" + nrOfTrial);
        result.add("The cost is same in " + sameCost + "/" + nrOfTrial);
        printtxt("Bremenpart1", result);
        /**
         * Stuttgart with 2 metrics: In average and 200 trials:
         * 
         * 
         * aStar with 1 landmark takes 534569997 nano secs while dij takes 198552274
         * nano secs.
         * 
         * Bremen with 2 metrics: In average and 200 trials:
         * 
         * 
         * aStar with 1 landmark takes 128580845 nano secs while dij takes 15190631 nano
         * secs. aStar visited nodes while dij visited nodes.
         * 
         * 
         * aStar with 2 landmark takes nano secs while dij takes nano secs. aStar
         * visited nodes while dij visited nodes.
         * 
         * aStar with 4 landmark takes nano secs while dij takes nano secs.
         * 
         * aStar with 8 landmark takes nano secs while dij takes nano secs.
         * 
         * aStar with 16 landmark takes nano secs while dij takes nano secs.
         */

        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        AStar_Standard aStarWithFourLandmark = new AStar_Standard(g, "ALT", 4);
        aStarWithTwoLandmark.setAlpha(alpha);
        aStarWithFourLandmark.setAlpha(alpha);
        long totalTimeAStarWithOneLandmark = 0;
        long totalTimeAStarWithTwoLandmark = 0;
        long totalTimeAStarWithFourLandmark = 0;
        long a2time;
        long a3time;
        nrOfTrial = 200;
        for (int i = 0; i < nrOfTrial; i++) {

            while (pathNotFound) {
                start = (int) (Math.random() * g.getNodeNr());// choose start and target randomly
                end = (int) (Math.random() * g.getNodeNr());
                aStar.setStart(start);
                aStar.setTarget(end);
                sTime = System.nanoTime();
                aStar.compute();
                eTime = System.nanoTime();
                atime = eTime - sTime;

                // astar with 2 landmark
                sTime = System.nanoTime();
                aStarWithTwoLandmark.compute();
                aStarWithTwoLandmark.getShortestPathInLonLat();
                eTime = System.nanoTime();
                a2time = eTime - sTime;
           

                // astar with 4 landmark
                sTime = System.nanoTime();
                aStarWithFourLandmark.compute();
                aStarWithFourLandmark.getShortestPathInLonLat();
                eTime = System.nanoTime();
                
                a3time = eTime - sTime;
            
                if (aStar.getPathAvailable() == false) {
                    totalTimeAStarWithTwoLandmark += a2time;
                    totalTimeAStarWithFourLandmark+= a3time;
                    totalTimeAStar += atime;
                    break;
                }
                aStar.reset();
            }

            start = (int) Math.random() * g.getNodeNr(); // choose a random start point
            end = (int) Math.random() * g.getNodeNr(); // choose a random target point
            aStar.setStart(start);// set the start
            aStar.setTarget(end);// set the target
            aStarWithTwoLandmark.setStart(start);// set the start
            aStarWithTwoLandmark.setTarget(end);// set the target
            aStarWithFourLandmark.setStart(start);// set the start
            aStarWithFourLandmark.setTarget(end);// set the target

            // astar with 1 landmark
            sTime = System.nanoTime();
            aStar.compute();
            aStar.getShortestPathInLonLat();
            eTime = System.nanoTime();
            time = eTime - sTime;
            totalTimeAStarWithOneLandmark += time;

        }
        long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / nrOfTrial;
        long averageTimeAStarWithFourLandmark = totalTimeAStarWithFourLandmark / nrOfTrial;
        System.out.println("aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds");
        System.out.println("aStar with ALT and two landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds");
        System.out.println("aStar with ALT and four landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithFourLandmark + "] nano seconds");
        result.add("aStar with ALT and one landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithOneLandmark + "] nano seconds");
        result.add("aStar with ALT and two landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds");
        result.add("aStar with ALT and four landmark Computation and path retrieval took in average ["
                + averageTimeAStarWithFourLandmark + "] nano seconds");
        printtxt("Bremenfull", result);
        /**
         * Bremen with 2 metrics: In average with 200 trials: 1 landmark: 341318 nano
         * secs 2 landmarks: 568448 nano secs 4 landmarks: 344786 nano secs
         */

    }
}
