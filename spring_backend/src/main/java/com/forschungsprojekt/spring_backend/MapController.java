package com.forschungsprojekt.spring_backend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
    // dummy string
    String cordinates = "(12,13),(23,34)";

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
    public String sendDipath(@RequestParam(name = "path") String path, @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end, @RequestParam(name="alpha") String alpha) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);

        return cordinates;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/astar")
    @ResponseBody
    public String sendAstar(@RequestParam(name = "path") String path, @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end) {
        decodedpath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        System.out.println(decodedpath);

        return cordinates;
    }

}
