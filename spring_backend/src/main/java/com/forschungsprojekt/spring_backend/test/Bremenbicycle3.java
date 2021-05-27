package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Bremenbicycle3 {
    public static List<String> result =new ArrayList<>();;
    public static void main(String[] args) {
        result.add("BREMEN bicycle 3---");
        Graph g = new Graph("/scratch/altprp/bremen_bicycle_3.graph");
        Testmethods.trialsAstar(g, result, "bremenbicycle_3_");

    }


  

    
}
