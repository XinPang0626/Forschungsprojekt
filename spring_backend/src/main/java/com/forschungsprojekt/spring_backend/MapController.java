package com.forschungsprojekt.spring_backend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class MapController {

    String cordinates="(12,13),(23,34)";

    @RequestMapping("/api")
    public String sendCordinates() {
        return cordinates;
    }  
    
}
