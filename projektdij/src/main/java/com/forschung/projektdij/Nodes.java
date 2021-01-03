package com.forschung.projektdij;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Nodes {
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private final double lang;
    private final double lat;

    public Nodes(double x, double y){
        this.lang=x;
        this.lat=y;
    }
    
    
    // standard constructors / setters / getters / toString
}