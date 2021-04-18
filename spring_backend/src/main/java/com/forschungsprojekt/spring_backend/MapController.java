package com.forschungsprojekt.spring_backend;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    // declare variables that should be accessed by every method in this
    static Graph graph;
    static Quadtree quadtree;
    static int landmarks;
    static AStar_Standard aStar_Standard;
    static AStar_Standard aStar_Alt;
    String decodedpath;
    int endPoint;
    int startPoint;
    // dummy string format be: [[long, lat),(long, lat),(long,lat).... due Geojson
    // reading in that format
    String cordinates = "[[-104.98809814453125, 39.76632525654491],[-104.9359130859375,39.751017451967144],[-104.974365234375, 39.720919782725545]]";
    public static void main(String[] args) {

		SpringApplication.run(SpringBackendApplication.class, args);
        ReadConfig.read("./spring_backend/src/main/resources/config.txt");
       graph= new Graph(ReadConfig.filepath);
       quadtree= new Quadtree(ReadConfig.filepath);
       landmarks= ReadConfig.nrLandmarks;

       aStar_Standard= new AStar_Standard(graph, "Standard", ReadConfig.nrLandmarks);
       aStar_Alt= new AStar_Standard(graph, "ALT", ReadConfig.nrLandmarks);
      
      
	}

    /**
     * @return moves to the center of the new map in frontend
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/api")
    @ResponseBody
    public String sendCordinates() {
        return "[[" + graph.getLongitude(0) + "," + graph.getLatitude(0) + "]]";
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/apicor")
    @ResponseBody
    public String sendPoint(@RequestParam(name = "point") String point, @RequestParam(name = "startOrend") String startOrend) {
        
        String[] latLon = point.split(" ");
        double[] doubleLatLon = Arrays.stream(latLon).mapToDouble(Double::parseDouble).toArray();
        double lat = doubleLatLon[0];
        double lon = doubleLatLon[1];
        int pointID = quadtree.nextNeighborWithReset(lat, lon);
        System.out.println(pointID + " lat;" + lat + " long:" + lon);
      
        if(startOrend.equals("start")){
            startPoint=pointID;
        }else if(startOrend.equals("end")){
            endPoint=pointID;
        }

        return "[[" + graph.getLongitude(pointID) + "," + graph.getLatitude(pointID) + "]]";
    }


    /**
     * calls Dijkstra algorithm when triggered
     * @param start id
     * @param end   id
     * @param alpha
     * @return cordinates 
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
     * aktualisiere mit neuen Node start und end feld nur nachdem man sendAstar
     * aufgerufen wird
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarpath")
    @ResponseBody
    public String resendAstar(@RequestParam(name = "start") int start, @RequestParam(name = "end") int end,  @RequestParam(name = "alpha") String alpha,
     @RequestParam(name = "type") String type) {
        String[] alphaStringArray = alpha.split(" ");
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();
        if(type.equals("Standard")){
            aStar_Standard.setStart(start);
            aStar_Standard.setTarget(end);
            aStar_Standard.setAlpha(doubleAlpha);
            aStar_Standard.compute();
            cordinates = aStar_Standard.getShortestPathInLonLat(end);

        }else if(type.equals("ALT")){
            aStar_Alt.setStart(start);
            aStar_Alt.setTarget(end);
            aStar_Alt.setAlpha(doubleAlpha);
            aStar_Alt.compute();
            cordinates = aStar_Alt.getShortestPathInLonLat(end);

        }
      
       
        System.out.println("A* " + " start with " + start + "-" + end);

        return cordinates;
    }

    /**
     *
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
    public String sendAstarcor(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha,
     @RequestParam(name = "type") String type) {
        // type is now Standard and ALT
        System.out.println("computing A* from "+startPoint+ "-"+endPoint);
        String[] alphaStringArray = alpha.split(" ");//split the value of cost vector in String
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();//convert String value to double

        if(type.equals("Standard")){
            aStar_Standard.setStart(startPoint);
            aStar_Standard.setTarget(endPoint);
            aStar_Standard.setAlpha(doubleAlpha);
            aStar_Standard.compute();
            cordinates = aStar_Standard.getShortestPathInLonLat(endPoint);

        }else if(type.equals("ALT")){
            aStar_Alt.setStart(startPoint);
            aStar_Alt.setTarget(endPoint);
            aStar_Alt.setAlpha(doubleAlpha);
            aStar_Alt.compute();
            cordinates = aStar_Alt.getShortestPathInLonLat(endPoint);

        }
        
        

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
        

        Dijkstra dij = new Dijkstra(quadtree.getGraph(), startPoint, doubleAlpha);
        cordinates = dij.getShortestPathInLonLat(endPoint);

        return cordinates;
    }

}
