package com.containment.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnalyseForiegnKeys {
	
	//This will return the tables which are having forign key relationships between differnt business objects
	public static List<List<String>> readTheForignKeyFile(String foriegnKeyLocation,String foriegnOperationLocation, List<List<String>>finalClusters){
		List<List<String>> allDetails = new ArrayList<List<String>>();
		
		ArrayList theFilterForignKeyTables = new ArrayList();
		ArrayList theRelatedOperations = new ArrayList();
		
		String totalTables = "";
		String totalOperations = "";
		
		try {
			
			FileReader fr = new FileReader(foriegnKeyLocation);
			BufferedReader br = new BufferedReader(fr);

			
			String st;
			while ((st = br.readLine()) != null) {
				totalTables = totalTables + st;
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		
try {
			
			FileReader fr1 = new FileReader(foriegnOperationLocation);
			BufferedReader br1 = new BufferedReader(fr1);

			
			String st1;
			while ((st1 = br1.readLine()) != null) {
				totalOperations = totalOperations + st1;
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		
		String splittedTables[] = totalTables.split("@@@");
		String splittedOperations[] = totalOperations.split("@@@");
		
		
		
		for(int i=0;i<splittedTables.length;i++){
			
			String splitOne[] = splittedTables[i].split("=");
			splitOne[0]= splitOne[0].replaceAll("\\s+", "");
			splitOne[1]= splitOne[1].replaceAll("\\s+", "");
			
			String splitTwo[] = splitOne[0].split("\\.");
			String splitThree[] = splitOne[1].split("\\.");
			
			//We have to check whether the tables in the foriegn key belong to the same business object
			int contained = 0;
			for(int j=0;j<finalClusters.size();j++){
				ArrayList temp = (ArrayList)finalClusters.get(j);
				
				if(temp.contains(splitTwo[0]) && temp.contains(splitThree[0])){
					contained = 1;
				}
				
				
			}
			
			if(contained == 0 && !theFilterForignKeyTables.contains(splitTwo[0]+"="+splitThree[0])){
					theFilterForignKeyTables.add(splitTwo[0]+"="+splitThree[0]);
					theRelatedOperations.add(splittedOperations[i]);
			}
			
			
		}
		
		allDetails.add(theFilterForignKeyTables);
		allDetails.add(theRelatedOperations);
		return allDetails;
		
	}

}
