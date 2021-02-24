package com.forschungsprojekt.spring_backend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import com.forschungsprojekt.spring_backend.localization.Quadtree;
import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {
    static Graph testgraph;
    Graph graph;
    Quadtree quadtree;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter path to File: ");
        String path = scanner.nextLine();
        testgraph = new Graph(path);
        scanner.close();
		SpringApplication.run(SpringBackendApplication.class, args);
	}
    String decodedpath;
    // dummy string format be: [[long, lat),(long, lat),(long,lat).... due Geojson reading in that format
    String cordinates = "[[-104.98809814453125, 39.76632525654491],[-104.9359130859375,39.751017451967144],[-104.974365234375, 39.720919782725545]]";
    
    // here to only revoke to get the nodes back at first
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/api")
    @ResponseBody
    public String sendCordinates(@RequestParam(name = "path") String path) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        graph= new Graph(decodedpath);
        quadtree = new Quadtree(decodedpath);
        return "[["+graph.getLongitude(0)+","+graph.getLatitude(0)+"]]";
    } // here to revoke algorithm and turn the result into string to be returned

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/dij")
    @ResponseBody
    public String sendDipath(@RequestParam(name = "path") String path, @RequestParam(name = "start") Integer start,
            @RequestParam(name = "end") Integer end,@RequestParam(name = "alpha") String alpha) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
                
        System.out.println(decodedpath+"----- "+start);
        
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        Dijkstra dij = new Dijkstra(graph, start, doubleAlpha);
        cordinates = dij.getShortestPathInLonLat(end);
        System.out.println(cordinates);
        return cordinates;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astar")
    @ResponseBody
    public String sendAstar(@RequestParam(name = "path") String path, @RequestParam(name = "start") int start,
            @RequestParam(name = "end") int end, @RequestParam(name = "alpha") String alpha, @RequestParam(name = "type") String type,
            @RequestParam(name = "landmark") int landmark, @RequestParam(name = "candidate") int candidate) {
                //type is now Standard and ALT
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
       
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
       AStar_Standard aStar = new AStar_Standard(graph, start, end, type, candidate);
       aStar.setAlpha(doubleAlpha);
       aStar.compute();
        cordinates = aStar.getShortestPathInLonLat(end);
        return cordinates;
    }


     //will receive a string of cordinates in form of 'lat,long' 
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarcor")
    @ResponseBody
    public String sendAstarcor(@RequestParam(name = "path") String path, @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha, @RequestParam(name = "type") String type,
            @RequestParam(name = "landmark") int landmark, @RequestParam(name = "candidate") int candidate) {
                //type is now Standard and ALT
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);//path decoding
        System.out.println(decodedpath);
        
        String[] alphaStringArray = alpha.split(" ");//split the value of cost vector in String
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();//convert String value to double
        String[] startLatLon = start.split(" ");//split the coordinate of start point from user
        double[] doubleStartLatLon = Arrays.stream(startLatLon).mapToDouble(Double::parseDouble).toArray();//convert coordinate to double value
        double startLat = doubleStartLatLon[0];//get the latitude of start point in double
        double startLon = doubleStartLatLon[1];//get the longtitude of start point in double
        int startPoint = quadtree.nextNeighborWithReset(startLat, startLon);//compute the coorespond point in datastructure/quadtree

        String[] endLatLon = end.split(" ");//split the coordinate of end point
        double[] doubleEndLatLon = Arrays.stream(endLatLon).mapToDouble(Double::parseDouble).toArray();//convert coordinate to double value
        double endLat = doubleEndLatLon[0];//get the latitude of end point in double
        double endLon = doubleEndLatLon[1];//get the longtitud of end point in double
        int endPoint = quadtree.nextNeighborWithReset(endLat, endLon);//compute the coorespind point in datastructure/quadtree

        AStar_Standard aStar = new AStar_Standard(quadtree.getGraph(), startPoint, endPoint,type, candidate);
        aStar.setAlpha(doubleAlpha);
        aStar.compute();
        cordinates = aStar.getShortestPathInLonLat(endPoint);

        return cordinates;
    }

     //will receive a string of cordinates in form of 'lat, long' 
     @CrossOrigin(origins = "http://localhost:4200")
     @RequestMapping("/dijcor")
     @ResponseBody
     public String sendDijcor(@RequestParam(name = "path") String path, @RequestParam(name = "start") String start,
             @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha) {
                 //type is now Standard and ALT
         decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
         System.out.println(decodedpath);
        
         String[] alphaStringArray = alpha.split(" ");
         double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
         String[] startLatLon = start.split(" ");
         double[] doubleStartLatLon = Arrays.stream(startLatLon).mapToDouble(Double::parseDouble).toArray();
         double startLat = doubleStartLatLon[0];
         double startLon = doubleStartLatLon[1];
         int startPoint = quadtree.nextNeighborWithReset(startLat, startLon);
         System.out.println(startPoint+" lat;"+startLat+" long:"+startLon);

         String[] endLatLon = end.split(" ");
         double[] doubleEndLatLon = Arrays.stream(endLatLon).mapToDouble(Double::parseDouble).toArray();
         double endLat = doubleEndLatLon[0];
         double endLon = doubleEndLatLon[1];
         int endPoint = quadtree.nextNeighborWithReset(endLat, endLon);
         System.out.println(endPoint+" lat;"+endLat+" long:"+endLon);
 
         Dijkstra dij = new Dijkstra(quadtree.getGraph(), startPoint, doubleAlpha);
         cordinates = dij.getShortestPathInLonLat(endPoint);
    
         return cordinates;
     }




}
