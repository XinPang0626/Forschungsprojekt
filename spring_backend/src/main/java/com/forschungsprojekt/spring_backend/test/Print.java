package com.forschungsprojekt.spring_backend.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Print {
  public static List<String> result =new ArrayList<>();
    
    public static void main(String[] args) {
      result.add("this is only executed");
      printtxt("name");
    }

    public static void printtxt(String name){
      System.out.println("x");
      try {

              FileWriter myWriter = new FileWriter("../project/Forschungsprojekt/spring_backend/src/main/resources/"+name+".txt");
  
              for (int i = 0; i < result.size(); i++) {
                  myWriter.write(result.get(i) + "\n");
              }
              myWriter.close();
              System.out.println("Successfully wrote to the file.");
  
          } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
          }  
     } 
}
