package com.forschungsprojekt.spring_backend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadConfig {
    static int  nrLandmarks;
    static String filepath;

    
    public static  void read(String path) {

        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(path);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                strLine= strLine.replaceAll("\\s+","");
                if(strLine.startsWith("nroflandmark")){
                   String landmark=  strLine.replace("nroflandmark=", "");
                 nrLandmarks= Integer.parseInt(landmark);
                    System.out.println("Number of landmarks "+nrLandmarks);

                }else if(strLine.startsWith("graphfilepath")){
                    filepath=  strLine.replace("graphfilepath=", "");
                    System.out.println("Current path graph used "+filepath);

                }
                // Print the content on the console
               // System.out.println(strLine);
            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }  
      
        
    }

  
    // TODO
    
}
