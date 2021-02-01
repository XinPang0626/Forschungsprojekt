package com.forschungsprojekt.spring_backend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.forschungsprojekt.spring_backend.localization.Quadtree;
import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {
    String decodedpath;
    // dummy string format be: (long, lat),(long, lat),(long,lat).... due Geojson reading in that format
    String cordinates = "[[-104.98809814453125, 39.76632525654491],[-104.9359130859375,39.751017451967144],[-104.974365234375, 39.720919782725545]]";
    
    // here to only revoke to get the nodes back at first
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/api")
    @ResponseBody
    public String sendCordinates(@RequestParam(name = "path") String path) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        Graph graph= new Graph(decodedpath);

        return cordinates;
    } // here to revoke algorithm and turn the result into string to be returned

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/dij")
    @ResponseBody
    public String sendDipath(@RequestParam(name = "path") String path, @RequestParam(name = "start") Integer start,
            @RequestParam(name = "end") Integer end,@RequestParam(name = "alpha") String alpha) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
                
        System.out.println(decodedpath+"----- "+start);
        Graph graph = new Graph(decodedpath);
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
            @RequestParam(name = "landmark") int landmark) {
                //type is now Standard and ALT
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        Graph graph = new Graph(decodedpath);
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        AStar_Standard aStar = new AStar_Standard(graph, start, end, doubleAlpha, type, landmark);
        cordinates = aStar.getShortestPathInLonLat(end);
        return cordinates;
    }


     //will receive a string of cordinates in form of 'lat,long' 
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarcor")
    @ResponseBody
    public String sendAstarcor(@RequestParam(name = "path") String path, @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha, @RequestParam(name = "type") String type,
            @RequestParam(name = "landmark") int landmark) {
                //type is now Standard and ALT
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        Quadtree quadtree = new Quadtree(decodedpath);
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        String[] startLatLon = start.split(", ");
        double[] doubleLatLon = Arrays.stream(startLatLon).mapToDouble(Double::parseDouble).toArray();
        double startLat = doubleLatLon[0];
        double startLon = doubleLatLon[1];
        int startPoint = quadtree.nextNeighbor(startLat, startLon);

        String[] endLatLon = end.split(", ");
        doubleLatLon = Arrays.stream(endLatLon).mapToDouble(Double::parseDouble).toArray();
        double endLat = doubleLatLon[0];
        double endLon = doubleLatLon[1];
        int endPoint = quadtree.nextNeighbor(endLat, endLon);

        AStar_Standard aStar = new AStar_Standard(quadtree.getGraph(), startPoint, endPoint, doubleAlpha, type, landmark);
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
         Quadtree quadtree = new Quadtree(decodedpath);
         String[] alphaStringArray = alpha.split(" ");
         double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
         String[] startLatLon = start.split(", ");
         double[] doubleLatLon = Arrays.stream(startLatLon).mapToDouble(Double::parseDouble).toArray();
         double startLat = doubleLatLon[0];
         double startLon = doubleLatLon[1];
         int startPoint = quadtree.nextNeighbor(startLat, startLon);

         String[] endLatLon = end.split(", ");
         doubleLatLon = Arrays.stream(endLatLon).mapToDouble(Double::parseDouble).toArray();
         double endLat = doubleLatLon[0];
         double endLon = doubleLatLon[1];
         int endPoint = quadtree.nextNeighbor(endLat, endLon);
 
         Dijkstra dij = new Dijkstra(quadtree.getGraph(), startPoint, doubleAlpha);
         cordinates = dij.getShortestPathInLonLat(endPoint);
    
         return cordinates;
     }




}
