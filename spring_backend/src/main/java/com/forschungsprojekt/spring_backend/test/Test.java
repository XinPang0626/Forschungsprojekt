package com.forschungsprojekt.spring_backend.test;

import java.lang.annotation.Target;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Test {
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation.
         * compute and print the time for precalculation.
         */

        //Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/graph-files/stuttgart-travel-time-distance.txt");//load large graph (bawu)
        Graph g = new Graph("/Users/xinpang/Desktop/Studium/5. Semester/Forschungsprojekt/FP/graph-files/bremen.txt");//load small graph (bremen)

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
		double[] alpha = {0.5, 0.5};
		aStar.setAlpha(alpha);
		long sTime;
		long eTime;
		long time;
        int nrOfVisitedNodeAStar = 0;
        int nrOfVisitedNodeDij = 0;
        for (int i = 0; i < nrOfTrial; i++) {
            System.out.println("Computing trial nr. " + i + " ...");
            //AStar:
            while(pathNotFound){
				start = (int) (Math.random() * g.getNodeNr());//choose start and target randomly
				end = (int) (Math.random() * g.getNodeNr());
				aStar.setStart(start);
				aStar.setTarget(end);
				sTime = System.nanoTime();
				aStar.compute();
				eTime = System.nanoTime();
				if(aStar.getPathAvailable() == false){
					time = eTime - sTime;
                    totalTimeAStar += time;
                    nrOfVisitedNodeAStar += aStar.getNrOfVisitedNodes();
					break;
				}
                aStar.reset();
			}
            
            //dijkstra:
            sTime = System.nanoTime();
            Dijkstra d = new Dijkstra(g, start, alpha, end);
		    eTime = System.nanoTime();
		    time = eTime - sTime;
            totalTimedij += time;

            nrOfVisitedNodeDij = d.getNrOfVisitedNodes();
            totalNrOfVisitedNodeDij += nrOfVisitedNodeDij;

            //compare cost and path:
            if(aStar.getCost(end) == d.getCost(end)){
                sameCost++;
            }
            if(aStar.getShortestPathInLonLat().equals(d.getShortestPathInLonLat())){
                samePath++;
            }else{
                System.out.println("start: "+start);
                System.out.println("target: "+end);
                System.out.println("cost aStar: " + aStar.getCost(end) + ", cost Dijkstra: " + d.getCost(end));
            }
            aStar.reset();
        }
        long averageTimeAStar = totalTimeAStar / samePath;
        long averageTimedij = totalTimedij  / samePath;
        double averageNrOfVisitedNodesAStar = totalNrOfVisitedNodeAStar / samePath;
        double averageNrOfVisitedNodesDij = totalNrOfVisitedNodeDij / samePath;
        System.out.println("aStar with ALT Computation took in average ["+averageTimeAStar+"] nano seconds");
        //System.out.println("aStar with ALT visited in average [" + averageNrOfVisitedNodesAStar +"] nodes");
        System.out.println("Dijkstra Computation took in average ["+averageTimedij+"] nano seconds");
        //System.out.println("Dijkstra visited in average [" + averageNrOfVisitedNodesDij +"] nodes");
        System.out.println("The path is same in "+ samePath +"/"+nrOfTrial);
        System.out.println("The cost is same in "+ sameCost +"/"+nrOfTrial);
        /**
         * Stuttgart with 2 metrics:
         * In average and 200 trials:
         * 
         * 
         * aStar with 1 landmark takes 534569997 nano secs while dij takes 198552274 nano secs.
         * 
         * Bremen with 2 metrics:
         * In average and 200 trials:
         * 
         * 
         * aStar with 1 landmark takes 128580845 nano secs while dij takes 15190631 nano secs.
         * aStar visited  nodes while dij visited  nodes.
         * 
         * 
         * aStar with 2 landmark takes  nano secs while dij takes  nano secs.
         * aStar visited  nodes while dij visited  nodes.
         * 
         * aStar with 4 landmark takes  nano secs while dij takes  nano secs.
         * 
         * aStar with 8 landmark takes  nano secs while dij takes  nano secs.
         * 
         * aStar with 16 landmark takes  nano secs while dij takes  nano secs.
         */



        /**
         * campare aStar with 1, 2 and 4 landmarks
         */
        // AStar_Standard aStarWithOneLandmark = new AStar_Standard(g, "ALT", 1);
        // AStar_Standard aStarWithTwoLandmark = new AStar_Standard(g, "ALT", 2);
        // AStar_Standard aStarWithFourLandmark = new AStar_Standard(g, "ALT", 4);
        // aStarWithOneLandmark.setAlpha(alpha);
        // aStarWithTwoLandmark.setAlpha(alpha);
        // aStarWithFourLandmark.setAlpha(alpha);
        // long totalTimeAStarWithOneLandmark = 0;
        // long totalTimeAStarWithTwoLandmark = 0;
        // long totalTimeAStarWithFourLandmark = 0;
        // nrOfTrial = 200;
        // for (int i = 0; i < nrOfTrial; i++) {
        //     start = (int) Math.random() * g.getNodeNr(); //choose a random start point
        //     target = (int) Math.random() * g.getNodeNr(); //choose a random target point
        //     aStarWithOneLandmark.setStart(start);// set the start
        //     aStarWithOneLandmark.setTarget(target);//set the target
        //     aStarWithTwoLandmark.setStart(start);// set the start
        //     aStarWithTwoLandmark.setTarget(target);//set the target
        //     aStarWithFourLandmark.setStart(start);// set the start
        //     aStarWithFourLandmark.setTarget(target);//set the target

        //     //astar with 1 landmark
        //     long sTime = System.nanoTime();
		//     aStarWithOneLandmark.compute();
        //     aStarWithOneLandmark.getShortestPathInLonLat(target);
		//     long eTime = System.nanoTime();
		//     long time = eTime - sTime;
        //     totalTimeAStarWithOneLandmark += time;

        //     //astar with 2 landmark
        //     sTime = System.nanoTime();
		//     aStarWithTwoLandmark.compute();
        //     aStarWithTwoLandmark.getShortestPathInLonLat(target);
		//     eTime = System.nanoTime();
		//     time = eTime - sTime;
        //     totalTimeAStarWithTwoLandmark += time;

        //     //astar with 4 landmark
        //     sTime = System.nanoTime();
        //     aStarWithFourLandmark.compute();
        //     aStarWithFourLandmark.getShortestPathInLonLat(target);
        //     eTime = System.nanoTime();
        //     time = eTime - sTime;
        //     totalTimeAStarWithFourLandmark += time;
        // }
        // long averageTimeAStarWithOneLandmark = totalTimeAStarWithOneLandmark / nrOfTrial;
        // long averageTimeAStarWithTwoLandmark = totalTimeAStarWithTwoLandmark / nrOfTrial;
        // long averageTimeAStarWithFourLandmark = totalTimeAStarWithFourLandmark / nrOfTrial;
        // System.out.println("aStar with ALT and one landmark Computation and path retrieval took in average ["+averageTimeAStarWithOneLandmark+"] nano seconds");
        // System.out.println("aStar with ALT and two landmark Computation and path retrieval took in average ["+averageTimeAStarWithTwoLandmark+"] nano seconds");
        // System.out.println("aStar with ALT and four landmark Computation and path retrieval took in average ["+averageTimeAStarWithFourLandmark+"] nano seconds");
        
        /**
         * Bremen with 2 metrics:
         * In average with 200 trials:
         * 1 landmark: 341318 nano secs
         * 2 landmarks: 568448 nano secs
         * 4 landmarks: 344786 nano secs
         */
        




    }
}
