package com.forschungsprojekt.spring_backend.test;

import java.util.ArrayList;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Saarlandcar3 {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

        Graph g6 = new Graph("/scratch/altprp/saarland_car_3.graph");
       

        

        result.add("---SAARLAND CAR 3 GRAPH---");
        Testmethods.trialsAstar(g6, result, "saarlandcar3_");
    }
        
    
}
