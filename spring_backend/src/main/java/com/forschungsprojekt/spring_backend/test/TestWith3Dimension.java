package com.forschungsprojekt.spring_backend.test;

import com.forschungsprojekt.spring_backend.localization.Quadtree;
import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class TestWith3Dimension {
    public static void main(String[] args) {
        Quadtree q = new Quadtree("saarland_car_3.graph");
        Graph g = q.getGraph();
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
