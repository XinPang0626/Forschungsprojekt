package com.forschungsprojekt.spring_backend;



public class Node {
  
    private long Id;
    private String cordinates;


    public Node(String cordinate){
        super();
        this.cordinates=cordinate;
    }


    public long getID(){
        return this.Id;
    }
    public void setID(long id){
         this.Id=id;
    }

    public String getcordinates(){
        return this.cordinates;
    }
    public void setCordinates(String cord){
         this.cordinates=cord;
    }
    
}
