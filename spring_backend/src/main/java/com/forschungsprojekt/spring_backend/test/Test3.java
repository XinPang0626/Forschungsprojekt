package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;



public class Test3 {
    static List<String> result = new ArrayList<>();
    public static void main(String[] args) {
        /**
         * initialize the graph using the large map and do precalculation. compute and
         * print the time for precalculation.
         */

       
        Graph g3 = new Graph("/scratch/altprp/bw_car_2.graph");
        Graph g4 = new Graph("/scratch/altprp/germany_car_2.graph");

       

        result.add("---BW CAR 2 GRAPH---");
        Testmethods.comparisonASTARdij(g3, result);
        Testmethods.trialsAstar(g3, result);

        result.add("---GERMANY CAR 2 GRAPH---");
        Testmethods.comparisonASTARdij(g4, result);
        Testmethods.trialsAstar(g4, result);

        
      

        try {

            FileWriter myWriter = new FileWriter("./spring_backend/src/main/resources/RESULT3.txt");

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

