package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;



public class Test1 {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

        Graph g5 = new Graph("/scratch/altprp/saarland_car_2.graph");
        Graph g6 = new Graph("/scratch/altprp/saarland_car_3.graph");
       

        result.add("---SAARLAND CAR 2 GRAPH---");
        Testmethods.comparisonASTARdij(g5, result);
        Testmethods.trialsAstar(g5, result);

        result.add("---SAARLAND CAR 3 GRAPH---");
        Testmethods.comparisonASTARdij(g6, result);
        Testmethods.trialsAstar(g6, result);

      

        try {

            FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/RESULT1.txt");

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
