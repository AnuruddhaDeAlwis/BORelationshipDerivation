package com.dynamic.analysis;

import java.util.ArrayList;
import java.util.List;

//This is the class to identify the containments based on the dynamic executions
public class IdentifyExclusiveContainments {

	
	/*
	 * operationsAndTables - The tables with the related operations 
	 * allTheBOTables - This contains all the tables related to differnt BOs
	 * allTheBOs - Contains all the BO names
	 * 
	 * This is to proces and extract only the operations related to INSERT and DELETE with their related UPDATES
	 * If the INSERTS are there then they are related to the creations of the BOS
	 * If the DELETES are there then they are related to the destruction of the BOS
	 * */
	public static List<List<String>>  procesContainment(List<List<String>> operationsAndTables, List<List<String>> allTheBOTables, ArrayList allTheBOs){
		
		for(int i = 0; i < operationsAndTables.size(); i++){
			
			ArrayList temps = (ArrayList)operationsAndTables.get(i);
			
			for(int j = 0; j < temps.size(); j++){
				
				System.out.print(temps.get(j)+",");
			}
			
			System.out.println("");
		}
		
		
		List<List<String>> createBOs = new ArrayList<List<String>>(); //contians the BOs which are related by create operations
		List<List<String>> deleteBOs = new ArrayList<List<String>>(); //contains the BOs which are relaed by the delete operations
		
		
		
		for(int i =0 ; i < operationsAndTables.size(); i++){
			ArrayList temp = (ArrayList)operationsAndTables.get(i);
			ArrayList tempTables = new ArrayList(); //to keep the table names temparary for processing
			int opType = 0; //if this is 1 then it is insert, if it is 2 then it is delete
			
			for(int j =0; j<temp.size(); j++){
				
				String splitOne[] = temp.get(j).toString().split("###");
				if(splitOne[1].equalsIgnoreCase("INSERT")){
					opType = 1;
					if(splitOne[0].contains("=")){
						String splitTwo[] = splitOne[0].split("=");
						tempTables.add(splitTwo[0]);
						tempTables.add(splitTwo[1]);
					}else{
						tempTables.add(splitOne[0]);
					}
				}else if(splitOne[1].equalsIgnoreCase("DELETE")){
					opType = 2;
					if(splitOne[0].contains("=")){
						String splitTwo[] = splitOne[0].split("=");
						tempTables.add(splitTwo[0]);
						tempTables.add(splitTwo[1]);
					}else{
						tempTables.add(splitOne[0]);
					}
					
				}else if(splitOne[1].equalsIgnoreCase("UPDATE")){
//					if(splitOne[0].contains("=")){
//						String splitTwo[] = splitOne[0].split("=");
//						tempTables.add(splitTwo[0]);
//						tempTables.add(splitTwo[1]);
//					}else{
//						tempTables.add(splitOne[0]);
//					}
					
				}
				
			}
			
			
			//Now here we have to process and identify the BO related to the opertions and check whether it is a create or delete operation
			if(opType == 1){
				createBOs.add(idnetifyOperationAndBOs(tempTables,allTheBOTables, allTheBOs));
			}else if(opType == 2){
				deleteBOs.add(idnetifyOperationAndBOs(tempTables,allTheBOTables, allTheBOs));
			}
		
		}
		
		return identifyTheContainmentTypes(createBOs,deleteBOs);
		
		
	}
	
	
	
	//Here we process the whether operation types and then 
	public static ArrayList idnetifyOperationAndBOs(ArrayList tempTables,  List<List<String>> allTheBOTables, ArrayList allTheBOs){

		ArrayList tempTables2 = tempTables;
		ArrayList boPosition = new ArrayList();
	
		
		for(int i = 0; i < allTheBOTables.size(); i++){
			
			ArrayList temp = (ArrayList)allTheBOTables.get(i);
			
			for(int j = 0; j < tempTables.size(); j++){
				if(temp.contains(tempTables.get(j))){
					tempTables.remove(tempTables.get(j));
					if(!boPosition.contains(i)){
						boPosition.add(i);
					}
				}
			}
			
			tempTables = tempTables2;
		}
		
		
		ArrayList boObjectRealated = new ArrayList();
		
		for(int i = 0; i < boPosition.size(); i++){
			boObjectRealated.add(allTheBOs.get((int)boPosition.get(i)));
		}
		
		
		return boObjectRealated;
		
	}
	
	
	
	/*
	 * Now we have to process the contianment types based on the information in createBOs and deleteBOs ArrayList
	 * createBOs - BOs for creating
	 * deleteBOs - BOs for deleting
	 * */
	public static List<List<String>> identifyTheContainmentTypes(List<List<String>> createBOs, List<List<String>> deleteBOs){
		
		List<List<String>> exclusivelyContained = new ArrayList<List<String>>();
		
		
		
		for(int i = 0; i < createBOs.size(); i++){
			
			ArrayList temp = (ArrayList)createBOs.get(i);
			
			for(int j = 0; j < deleteBOs.size(); j++){
				
				ArrayList temp2 = (ArrayList)deleteBOs.get(j);
				ArrayList temporary = new ArrayList();
				
				for(int k = 0; k < temp.size(); k++){
					
					if(temp2.contains(temp.get(k))){
						temporary.add(temp.get(k));
					}
				}
				
				if(temporary.size() > 1 && !exclusivelyContained.contains(temporary)){
					exclusivelyContained.add(temporary);
				}
			}
			
		}
		
		
		
		
		
		
		
		return exclusivelyContained;
	}
	
}
