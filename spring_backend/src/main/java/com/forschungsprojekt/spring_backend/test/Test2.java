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

        Graph g1 = new Graph("/scratch/altprp/bawu_bicycle_3.graph");
        Graph g2 = new Graph("/scratch/altprp/bawu_car_3.graph");
       

        result.add("---BAWU BICYCLE 3 GRAPH---");
        Testmethods.comparisonASTARdij(g1, result);
        Testmethods.trialsAstar(g1, result);

        result.add("---BAWU CAR 3 GRAPH---");
        Testmethods.comparisonASTARdij(g2, result);
        Testmethods.trialsAstar(g2, result);

      

        try {

            FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/RESULT2.txt");

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
    
}
