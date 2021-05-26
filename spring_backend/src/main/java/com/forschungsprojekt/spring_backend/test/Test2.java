package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;



public class Test2 {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

    
        Graph g2 = new Graph("/scratch/altprp/bawu_car_3.graph");
       

       
        result.add("---BAWU CAR 3 GRAPH---");
        
        Testmethods.trialsAstar(g2, result, "bawu_car3_");

    }
    
}
