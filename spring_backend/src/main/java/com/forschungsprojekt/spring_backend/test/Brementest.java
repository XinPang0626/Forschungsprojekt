package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class Brementest {
    public static List<String> result =new ArrayList<>();
    public static void main(String[] args) {
        result.add("BREMEN CAR 2---");
        Graph g = new Graph("/scratch/altprp/bremen_car_2.graph");
        //Testmethods.trialsAstar(g5, result, "bremencar2_");
        //Quadtree q = new Quadtree("/scratch/altprp/bremen_car_2.graph");
       // Graph g = q.getGraph();
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
        aStar.setStart(1234);
        aStar.setTarget(5342);
        double[] alpha = {0.33,0.33,0.33};
        aStar.setAlpha(alpha);
        aStar.compute();
        String path = aStar.getShortestPathInLonLat(5342);
        System.out.println(path);

    }


}