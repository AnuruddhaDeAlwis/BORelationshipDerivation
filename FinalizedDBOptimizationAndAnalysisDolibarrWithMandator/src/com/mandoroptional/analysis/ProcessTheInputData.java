package com.mandoroptional.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessTheInputData {
	
	/*
	 * This is the class which we use to read the values from the files which contains the table names related to each BO and 
	 * the primary key and foriegn key related to each table*/
	
	public static void processingTheDataFile(String location, List<List<String>> operationsAndTables, String bo1, String bo2){
		
		ArrayList bo = new ArrayList(); //This contains the name of the BOS
		List<List<String>> boTableNames = new ArrayList<List<String>>();
		List<List<String>> boTablePKeys = new ArrayList<List<String>>(); //PrimaryKeys of the Table
		List<List<String>> boTableFKeys = new ArrayList<List<String>>(); //ForeignKeys of the Table
		
		String totalText = "";
		  try {
			  File file = new File(location); 
			  
			  BufferedReader br = new BufferedReader(new FileReader(file)); 
			  
			  
			  
			  String st; 
			while ((st = br.readLine()) != null){ 
				  totalText = totalText +st;
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		  
		  String splitOne[] = totalText.split("@@@"); //This contains all the details related to all the BOS
		  
		  //Now we try to seperate the datails related to each BO
		  for(int i = 0; i < splitOne.length; i++){
			  String splitTwo[] = splitOne[i].split("###"); 
			  bo.add(splitTwo[0]);
			  
			  
			  String splitThree[] = splitTwo[1].split(","); //This contains each table with realted data
			  ArrayList tempTables = new ArrayList();
			  ArrayList tempPKeys = new ArrayList();
			  ArrayList tempFKeys = new ArrayList();
			  for(int j =0; j < splitThree.length; j++){
				  String splitFour[] = splitThree[j].split("&&&");//This defines the table names and keys
				  tempTables.add(splitFour[0]);
				  
				  String splitFive[] = splitFour[1].split("@&@"); // splitFive[0] contains primarykeys and splitFive[1] foriegnkeys
				  
				  tempPKeys.add(splitFive[0]);
				  tempFKeys.add(splitFive[1]);
				  
			  }
			  
			  boTableNames.add(tempTables); //The table names related to each BO
			  boTablePKeys.add(tempPKeys); //The primary keys "rowid/tid"
			  boTableFKeys.add(tempFKeys);//The Foriegn keys "fk_parent/fk_user_c/fk_user_m"
		  }
		  
		  ArrayList foriegnKeyTables = processTheTables(operationsAndTables);
		  ArrayList theRelatedTables = identifyTheRelatedTables(foriegnKeyTables, boTableNames, bo, bo1, bo2);
		  System.out.println("Test");
		
	}
	
	
	
	public static ArrayList processTheTables(List<List<String>> operationsAndTables){
		ArrayList foriegnKeyTables = new ArrayList();
		
		for(int i =0; i < operationsAndTables.size(); i++ ){
			ArrayList temp = (ArrayList)operationsAndTables.get(i);
			
			for(int j =0; j < temp.size(); j++){
				
				if(temp.get(j).toString().contains("=")){
					foriegnKeyTables.add(temp.get(j));
				}			
			}
			
		}
		
		
		return foriegnKeyTables;
	}
	
	
	
	public static ArrayList identifyTheRelatedTables(ArrayList foriegnKeyTables, List<List<String>> boTableNames, ArrayList bo,String bo1, String bo2){
		
		ArrayList theRelatedTables = new ArrayList();
		
		int bo1Position = bo.indexOf(bo1);
		int bo2Position = bo.indexOf(bo2);
		
		ArrayList bo1Tables = (ArrayList)boTableNames.get(bo1Position);
		ArrayList bo2Tables = (ArrayList)boTableNames.get(bo2Position);
		
		ArrayList fKeystoAnalyse = new ArrayList(); //These are the foriegnkeys between the BOs that we analyse
		
		for(int i =0; i < foriegnKeyTables.size(); i++){
			
			String splitOne[] =  foriegnKeyTables.get(i).toString().split("###");
			String splitTwo[] = splitOne[0].split("=");
			
			if(splitTwo.length > 1){
				if((bo1Tables.contains(splitTwo[0]) && bo2Tables.contains(splitTwo[1])) || (bo1Tables.contains(splitTwo[1]) && bo2Tables.contains(splitTwo[0]))){
					String key = splitTwo[0]+"="+splitTwo[1];
					theRelatedTables.add(key);
				}
			}
			
		}
		
		return theRelatedTables;
	}

}
