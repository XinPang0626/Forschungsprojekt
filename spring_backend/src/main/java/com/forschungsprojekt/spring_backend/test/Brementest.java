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
       
        AStar_Standard aStar = new AStar_Standard(g, "ALT", 1);
        aStar.setStart(3);
        aStar.setTarget(5);
        double[] alpha = {0.4,0.3,0.3};
        aStar.setAlpha(alpha);
        aStar.compute();
        //String path = aStar.getShortestPathInLonLat(5);
        //System.out.println(path);

    }


}