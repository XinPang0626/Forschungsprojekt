package com.forschungsprojekt.spring_backend.test;

import java.util.ArrayList;
import java.util.List;

import com.forschungsprojekt.spring_backend.routerplaner.Graph;

public class BigGraph {
    static List<String> result = new ArrayList<>();

    public static void main(String[] args) {
    result.add("Germany CAR 2---");
        Graph g4 = new Graph("/scratch/altprp/germany_car_2.graph");
        Testmethods.trialsAstar(g4, result, "Germany_car2_");
    
}
}
