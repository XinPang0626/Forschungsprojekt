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
        long sTime;
        long eTime;
        long time;
        long dtime = 0;
        long atime = 0;

        boolean pathNotFound = true;
        int start = 0;
        int end = 0;

        sTime = System.nanoTime();
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
        eTime = System.nanoTime();
        time = eTime - sTime;
        result.add("Pre calc for 1 landmark: " + time);

        sTime = System.nanoTime();
        AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);

        eTime = System.nanoTime();
        time = eTime - sTime;
        result.add("Pre calc for 2 landmark: " + time);

        sTime = System.nanoTime();
        AStar_Standard aStarWithFourLandmark = new AStar_Standard(g, "ALT", 4);

        eTime = System.nanoTime();
        time = eTime - sTime;
        result.add("Pre calc for 4 landmark: " + time);

        sTime = System.nanoTime();
        AStar_Standard aStarWithEightLandmark = new AStar_Standard(g, "ALT", 8);

        eTime = System.nanoTime();
        time = eTime - sTime;
        result.add("Pre calc for 8 landmark: " + time);

        double[] alpha = { 0.5, 0.5 };

        aStar.setAlpha(alpha);
        aStarWithTwoLandmark.setAlpha(alpha);
        aStarWithFourLandmark.setAlpha(alpha);
        aStarWithEightLandmark.setAlpha(alpha);

        long totalTimeAStarWithTwoLandmark = 0;
        long totalTimeAStarWithFourLandmark = 0;
        long totalTimeAStarWithEightLandmark = 0;
        long a2time = 0;
        long a4time = 0;
        long a8time = 0;

        int nrOfVisitedNodeAStar = 0;
        int nrOfVisitedNodeDij = 0;
        for (int i = 0; i < nrOfTrial; i++) {
            System.out.println("Computing trial nr. " + i + " ...");
            // AStar:
            while (pathNotFound) {
                start = (int) (Math.random() * g.getNodeNr());// choose start and target randomly
                end = (int) (Math.random() * g.getNodeNr());

                // landmark 1
                aStar.setStart(start);
                aStar.setTarget(end);
                sTime = System.nanoTime();
                aStar.compute();
                eTime = System.nanoTime();
                atime = eTime - sTime;

                // astar with 2 landmark
                aStarWithTwoLandmark.setStart(start);// set the start
                aStarWithTwoLandmark.setTarget(end);// set the target
                sTime = System.nanoTime();
                aStarWithTwoLandmark.compute();

                eTime = System.nanoTime();
                a2time = eTime - sTime;

                // astar with 4 landmark
                aStarWithFourLandmark.setStart(start);// set the start
                aStarWithFourLandmark.setTarget(end);// set the target
                sTime = System.nanoTime();
                aStarWithFourLandmark.compute();

                eTime = System.nanoTime();
                a4time = eTime - sTime;

                // astar with 8 landmark
                aStarWithEightLandmark.setStart(start);// set the start
                aStarWithEightLandmark.setTarget(end);// set the target
                sTime = System.nanoTime();
                aStarWithEightLandmark.compute();
                eTime = System.nanoTime();

                a8time = eTime - sTime;

                if (aStar.getPathAvailable() == false) {

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

            nrOfVisitedNodeDij = d.getNrOfVisitedNodes();
            totalNrOfVisitedNodeDij += nrOfVisitedNodeDij;

            // compare cost and path:
            if (aStar.getCost(end) == d.getCost(end)) {

                sameCost++;
            }
            if (aStar.getShortestPathInLonLat().equals(d.getShortestPathInLonLat())) {
                totalTimeAStar += atime;
                totalTimeAStarWithTwoLandmark += a2time;
                totalTimeAStarWithFourLandmark += a4time;
                totalTimeAStarWithEightLandmark += a8time;
                totalTimeAStar += atime;
                totalTimedij += dtime;
                samePath++;

            } else {
                System.out.println("start: " + start);
                System.out.println("target: " + end);
                System.out.println("cost aStar: " + aStar.getCost(end) + ", cost Dijkstra: " + d.getCost(end));
            }
            aStar.reset();
        }

        long averageTimedij = totalTimedij / samePath;
        double averageNrOfVisitedNodesAStar = totalNrOfVisitedNodeAStar / samePath;
        double averageNrOfVisitedNodesDij = totalNrOfVisitedNodeDij / samePath;

        long averageTimeAStar = totalTimeAStar / samePath;
        long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / samePath;
        long averageTimeAStarWithFourLandmark = totalTimeAStarWithFourLandmark / samePath;
        long averageTimeAStarWithEightLandmark = totalTimeAStarWithEightLandmark / samePath;

        String astartext = "aStar with ALT and one landmark Computation took in average [" + averageTimeAStar
                + "] nano seconds";
        String astartext2 = "aStar with ALT and two landmark Computation  took in average ["
                + averageTimeAStarWithTwoLandmark + "] nano seconds";
        String astartext4 = "aStar with ALT and four landmark Computation took in average ["
                + averageTimeAStarWithFourLandmark + "] nano seconds";
        String astartext8 = "aStar with ALT and eight landmark Computation took in average ["
                + averageTimeAStarWithEightLandmark + "] nano seconds";

        String dijtext = "Dijkstra Computation took in average [" + averageTimedij + "] nano seconds";

        System.out.println(dijtext);
        System.out.println(astartext);
        System.out.println(astartext2);
        System.out.println(astartext4);
        System.out.println(astartext8);

        result.add(dijtext);
        result.add(astartext);
        result.add(astartext2);
        result.add(astartext4);
        result.add(astartext8);

        System.out.println("The path is same in " + samePath + "/" + nrOfTrial);
        System.out.println("The cost is same in " + sameCost + "/" + nrOfTrial);

        result.add("The path is same in " + samePath + "/" + nrOfTrial);
        result.add("The cost is same in " + sameCost + "/" + nrOfTrial);

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

        printtxt("Bremenfull", result);
        /**
         * Bremen with 2 metrics: In average with 200 trials: 1 landmark: 341318 nano
         * secs 2 landmarks: 568448 nano secs 4 landmarks: 344786 nano secs
         */

    }
}
