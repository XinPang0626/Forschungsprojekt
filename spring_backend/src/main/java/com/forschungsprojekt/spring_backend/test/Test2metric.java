package com.forschungsprojekt.spring_backend.test;

import java.util.ArrayList;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Test2metric {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */
        result.add("BREMEN CAR 2---");
        Graph g1 = new Graph("/scratch/altprp/bremen.graph");
        Testmethods.trialsAstar(g1, result, "Bremencar2_");

        Graph g = new Graph("/scratch/altprp/saarland_car_2.graph");
        result.add("---SAARLAND CAR 2 GRAPH---");
        Testmethods.trialsAstar(g, result, "saarlandcar2_");

        //stuttgart-travel-time-distance
        result.add("Stuttgart CAR 2---");
        Graph g2 = new Graph("/scratch/altprp/stuttgart-travel-time-distance.graph");
        Testmethods.trialsAstar(g2, result, "Stuttgart2_");

        result.add("Stuttgart  1---");
        Graph g3 = new Graph("/scratch/altprp/stuttgart-travel-time.graph");
        Testmethods.trialsAstar(g3, result, "Stuttgart1_");

        result.add("BW CAR 1---");
        Graph g5 = new Graph("/scratch/altprp/bawü-travel-time.graph");
        Testmethods.trialsAstar(g5, result, "BW_car1_");

         result.add("BW CAR 2---");
        Graph g6 = new Graph("/scratch/altprp/bw_car_2.graph");
        Testmethods.trialsAstar(g6, result, "BW_car2_");

        result.add("Germany CAR 2---");
        Graph g4 = new Graph("/scratch/altprp/germany_car_2.graph");
        Testmethods.trialsAstar(g4, result, "Germany_car2_");
       

       

        
    }
    
}
