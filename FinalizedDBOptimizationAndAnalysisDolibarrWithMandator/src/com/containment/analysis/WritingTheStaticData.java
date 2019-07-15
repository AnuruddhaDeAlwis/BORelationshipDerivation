package com.containment.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class WritingTheStaticData {
	
	public static void writingTheStaticData(final File folder, String WriteTo){
		 String allInfromation = "";
		for (final File fileEntry : folder.listFiles()) {
          
        if (fileEntry.isDirectory()) {
        	writingTheStaticData(fileEntry,WriteTo);
        } else {
            //System.out.println(fileEntry.getName());
            //System.out.println(fileEntry.getAbsolutePath())
            String ext1 = FilenameUtils.getExtension(fileEntry.getAbsolutePath());
          
            	
            	
            try {
		    BufferedReader in = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
		    String str;
		    while ((str = in.readLine()) != null){
		    	allInfromation = allInfromation + str;
		    	}
		    in.close();
		    
		} catch (IOException e) {
		}
                
            

            	
            
            
            
        }
        
    }
		
		writeTheStaticData(allInfromation, WriteTo);
	}
	
	
	
	public static void writeTheStaticData(String datatoWrite, String WriteTo){
		try {
 	    	FileWriter writer = new FileWriter(WriteTo, true);
			writer.write(datatoWrite);
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
