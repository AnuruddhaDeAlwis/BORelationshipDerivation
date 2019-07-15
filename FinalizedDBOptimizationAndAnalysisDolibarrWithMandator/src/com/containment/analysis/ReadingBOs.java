package com.containment.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;

public class ReadingBOs {

	public static List<List<String>> readindTheBusinessObjects(String fileLocation) {
		
		String allTheBos = "";
		
		try {

			FileReader fr = new FileReader(fileLocation);
			BufferedReader br = new BufferedReader(fr);

			String st;
			while ((st = br.readLine()) != null) {
				allTheBos = allTheBos + st;
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		return businessObjectsIdentified(allTheBos);
	}
	
	
	
	public static List<List<String>> businessObjectsIdentified(String allTheBos){
		
		 List<List<String>> allTheBOs = new ArrayList<List<String>>();
		
		allTheBos = allTheBos.replace("\n", "").replace("\r", "");
		allTheBos = allTheBos.replaceAll("\\s+", "");
		
		String splitOne[] = allTheBos.split("@@@");
		
		
		for(int i=0; i<splitOne.length;i++){
			String splitTwo[] = splitOne[i].split(",");
			
			ArrayList tempArray = new ArrayList();
			
			for(int j=0;j<splitTwo.length;j++){
				tempArray.add(splitTwo[j]);
			}
			
			allTheBOs.add(tempArray);
			
			
		}
		
		
		return allTheBOs;
	} 
	

}
