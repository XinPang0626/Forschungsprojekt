package com.forschungsprojekt.spring_backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    Quadtree quadtree;
    AStar_Standard aStar;
    String decodedpath;
    int endPoint;
    int startPoint;
    // dummy string format be: [[long, lat),(long, lat),(long,lat).... due Geojson
    // reading in that format
    String cordinates = "[[-104.98809814453125, 39.76632525654491],[-104.9359130859375,39.751017451967144],[-104.974365234375, 39.720919782725545]]";
    public static void main(String[] args) {
		SpringApplication.run(SpringBackendApplication.class, args);
        graph= new Graph("./spring_backend/src/main/resources/bremen.graph");
        // spring_backend\src\main\resources\bremen.graph
        //./spring_backend/src/main/resources/bremen.graph
	}

    /**
     * @param path builds graph and quadtree once, so it doesn't need to be builld
     * @return moves to the center of the new map in frontend
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/api")
    @ResponseBody
    public String sendCordinates(@RequestParam(name = "path") String path) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);
        //graph = new Graph(decodedpath);
        quadtree = new Quadtree(decodedpath);
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
     * load the first pre-calculation of A* 
     * 
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astarload")
    @ResponseBody
    public String sendAstar(  @RequestParam(name = "type") String type) {
        // type is now Standard or ALT
        int landmark=2;
        try {
			BufferedReader br = new BufferedReader(new FileReader("./spring_backend/src/main/resources/NrLandmarks.txt"));
			 String line = br.readLine();
             landmark= Integer.parseInt(line);
            System.out.println(landmark); 
            aStar = new AStar_Standard(graph, type, landmark);
            br.close();
        }catch (IOException e) {
			e.printStackTrace();
		}
        return "loaded";
    }

    /**
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
    public String sendAstarcor(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end, @RequestParam(name = "alpha") String alpha) {
        // type is now Standard and ALT
        System.out.println("computing A* from "+startPoint+ "-"+endPoint);
        String[] alphaStringArray = alpha.split(" ");//split the value of cost vector in String
        double[] doubleAlpha = Arrays.stream(alphaStringArray).mapToDouble(Double::parseDouble).toArray();//convert String value to double
        
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
        

        Dijkstra dij = new Dijkstra(quadtree.getGraph(), startPoint, doubleAlpha);
        cordinates = dij.getShortestPathInLonLat(endPoint);

        return cordinates;
    }

}
