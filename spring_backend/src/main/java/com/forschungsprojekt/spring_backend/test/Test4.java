package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;



public class Test4 {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

       
        Graph g7 = new Graph("/scratch/altprp/saarland_bicycle_3.graph");

        

        result.add("---SAARLAND BICYCLE 3 GRAPH---");
       
        Testmethods.trialsAstar(g7, result, "saarlandbi3_");
      

    }
    
}

