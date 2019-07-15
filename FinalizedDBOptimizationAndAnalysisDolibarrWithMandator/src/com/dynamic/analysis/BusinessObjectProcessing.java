package com.dynamic.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BusinessObjectProcessing {
	
public static ArrayList readindTheBusinessObjects(String fileLocation) {
		
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



	public static List<List<String>> readindTheBusinessObjectsTables(String fileLocation) {
		
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
		
		return businessObjectsTableIdentified(allTheBos);
	}
	
	
		
	public static ArrayList businessObjectsIdentified(String allTheBos){
		
		 ArrayList allTheBOs = new ArrayList();
		
		allTheBos = allTheBos.replace("\n", "").replace("\r", "");
		allTheBos = allTheBos.replaceAll("\\s+", "");
		
		String splitOne[] = allTheBos.split("@@@");
		
		
		for(int i=0; i<splitOne.length;i++){
			String splitTwo[] = splitOne[i].split("###");
			allTheBOs.add(splitTwo[0]);
			
			
		}
		
		
		return allTheBOs;
	} 
	
	
	
	public static List<List<String>> businessObjectsTableIdentified(String allTheBos){
		
		 List<List<String>> allTheBOs = new ArrayList<List<String>>();
		
		allTheBos = allTheBos.replace("\n", "").replace("\r", "");
		allTheBos = allTheBos.replaceAll("\\s+", "");
		
		String splitOne[] = allTheBos.split("@@@");
		
		
		for(int i=0; i<splitOne.length;i++){
			String splitTwo[] = splitOne[i].split("###");
			String splitThree[] = splitTwo[1].split(",");
			
			ArrayList tempArray = new ArrayList();
			
			for(int j=0;j<splitThree.length;j++){
				tempArray.add(splitThree[j]);
			}
			
			allTheBOs.add(tempArray);
			
			
		}
		
		
		return allTheBOs;
	} 


}
