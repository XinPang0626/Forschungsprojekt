package com.forschungsprojekt.spring_backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class MapController {

    String cordinates="(12,13),(23,34)";
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/api")
    public String sendCordinates(@RequestParam(name="path") String path) {
        System.out.println(path);
        return cordinates;
    }  
    
}
