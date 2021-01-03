package com.forschung.projektdij;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NodeController {

    private  NodeRepository nodeRepository;


    @GetMapping("/nodes")
    public List<Nodes> getNodes() {
        return (List<Nodes>) nodeRepository.findAll();
    }

    @PostMapping("/nodes")
    void addUser(@RequestBody Nodes node) {
        nodeRepository.save(node);
    }
    
}
