package com.forschungsprojekt.spring_backend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import com.forschungsprojekt.spring_backend.localization.Quadtree;
import com.forschungsprojekt.spring_backend.routerplaner.AStar_Standard;
import com.forschungsprojekt.spring_backend.routerplaner.Dijkstra;
import com.forschungsprojekt.spring_backend.routerplaner.Graph;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {
    // declare variables that should be accessed by every method in this
    Graph graph;
    Quadtree quadtree;
    AStar_Standard aStar;


    String decodedpath;
    // dummy string format be: [[long, lat),(long, lat),(long,lat).... due Geojson
    // reading in that format
    String cordinates = "[[-104.98809814453125, 39.76632525654491],[-104.9359130859375,39.751017451967144],[-104.974365234375, 39.720919782725545]]";

    /**
     * 
     * @param path builds graph and quadtree once, so it doesn't need to be builld
     * @return moves to the center of the new map in frontend
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/api")
    @ResponseBody
    public String sendCordinates(@RequestParam(name = "path") String path) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        graph = new Graph(decodedpath);
        quadtree = new Quadtree(decodedpath);
        return "[[" + graph.getLongitude(0) + "," + graph.getLatitude(0) + "]]";
    }

    /**
     * calls Dijkstra algorithm when triggered
     * 
     * @param start id
     * @param end   id
     * @param alpha
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/dij")
    @ResponseBody
    public String sendDipath(@RequestParam(name = "start") Integer start, @RequestParam(name = "end") Integer end,
            @RequestParam(name = "alpha") String alpha) {

        System.out.println("dijkstra start with " + start + "-" + end);

        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        Dijkstra dij = new Dijkstra(graph, start, doubleAlpha);
        cordinates = dij.getShortestPathInLonLat(end);
        System.out.println(cordinates);
        return cordinates;
    }

    /**
     * TODO load the first pre-calculation of A* 
     * 
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarload")
    @ResponseBody
    public String sendAstar(  @RequestParam(name = "type") String type,
            @RequestParam(name = "landmark") int landmark) {
        // type is now Standard and ALT

        aStar = new AStar_Standard(graph, type, landmark);
        return "loaded";
    }

    /**TODO
     * aktualisiere mit neuen Node start und end feld nur nachdem man sendAstar
     * aufgerufen wird
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarpath")
    @ResponseBody
    public String resendAstar(@RequestParam(name = "start") int start, @RequestParam(name = "end") int end,  @RequestParam(name = "alpha") String alpha) {
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        aStar.setStart(start);
        aStar.setTarget(end);
        aStar.setAlpha(doubleAlpha);
        aStar.compute();
         cordinates = aStar.getShortestPathInLonLat(end);
        System.out.println("A* " + " start with " + start + "-" + end);

        return cordinates;
    }

    /**
     * TODO: Falls loading auch für Koordinaten senden gilt, dann kann wird später die restlichen Paramter entfernt, Sodass diese nur start und end parameter hat
     * @param start
     * @param end
     * @param alpha
     * @param type
     * @param landmark
     * @param candidate
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarcor")
    @ResponseBody
    public String sendAstarcor(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha) {
        // type is now Standard and ALT
        System.out.println("computing A*");

    
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

        
        aStar.setStart(startPoint);
        aStar.setTarget(endPoint);
        aStar.setAlpha(doubleAlpha);
        aStar.compute();
        cordinates = aStar.getShortestPathInLonLat(endPoint); 
        
        

        return cordinates;
    }

    // will receive a string of cordinates in form of 'lat, long'
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/dijcor")
    @ResponseBody
    public String sendDijcor(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end,
            @RequestParam(name = "alpha") String alpha) {

        System.out.println("dijkstra cordinates start with " + start + "-" + end);

        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        String[] startLatLon = start.split(" ");
        double[] doubleStartLatLon = Arrays.stream(startLatLon).mapToDouble(Double::parseDouble).toArray();
        double startLat = doubleStartLatLon[0];
        double startLon = doubleStartLatLon[1];
        int startPoint = quadtree.nextNeighborWithReset(startLat, startLon);
        System.out.println(startPoint + " lat;" + startLat + " long:" + startLon);

        String[] endLatLon = end.split(" ");
        double[] doubleEndLatLon = Arrays.stream(endLatLon).mapToDouble(Double::parseDouble).toArray();
        double endLat = doubleEndLatLon[0];
        double endLon = doubleEndLatLon[1];
        int endPoint = quadtree.nextNeighborWithReset(endLat, endLon);
        System.out.println(endPoint + " lat;" + endLat + " long:" + endLon);

        Dijkstra dij = new Dijkstra(quadtree.getGraph(), startPoint, doubleAlpha);
        cordinates = dij.getShortestPathInLonLat(endPoint);

        return cordinates;
    }

}
