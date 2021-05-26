package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Test {
    static List<String> result = new ArrayList<>();

    public static void main(String[] args) {
       
         

        Graph g1 = new Graph("/scratch/altprp/bawu_bicycle_3.graph");
        result.add("---BAWU BICYCLE 3 GRAPH---");
        Testmethods.trialsAstar(g1, result, "bawu_bicycle3_");

        

    }
}
