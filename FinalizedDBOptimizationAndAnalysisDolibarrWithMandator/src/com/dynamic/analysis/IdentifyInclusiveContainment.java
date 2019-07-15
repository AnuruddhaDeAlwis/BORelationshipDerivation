package com.dynamic.analysis;

import java.util.ArrayList;
import java.util.List;

public class IdentifyInclusiveContainment {
	
	 /* 
	  * operationsAndTables - The tables with the related operations 
	 * allTheBOTables - This contains all the tables related to differnt BOs
	 * allTheBOs - Contains all the BO names
	 * 
	 * This is to proces and extract only the operations related to INSERT and DELETE with their related UPDATES
	 * If the INSERTS are there then they are related to the creations of the BOS
	 * If the DELETES are there then they are related to the destruction of the BOS
	 * */
	public static List<List<String>>  procesContainment(List<List<String>> operationsAndTables, List<List<String>> allTheBOTables, ArrayList allTheBOs, List<List<String>> exclusivelyContained){
		
		for(int i = 0; i < operationsAndTables.size(); i++){
			
			ArrayList temps = (ArrayList)operationsAndTables.get(i);
			
			for(int j = 0; j < temps.size(); j++){
				
				System.out.print(temps.get(j)+",");
			}
			
			System.out.println("");
		}
		
		
		List<List<String>> createBOs = new ArrayList<List<String>>(); //contians the BOs which are related by create operations
		
		
		
		
		for(int i =0 ; i < operationsAndTables.size(); i++){
			ArrayList temp = (ArrayList)operationsAndTables.get(i);
			ArrayList tempTables = new ArrayList(); //to keep the table names temparary for processing
//			String opType = ""; //if this is 1 then it is insert, if it is 2 then it is delete
			
			for(int j =0; j<temp.size(); j++){
				
				String splitOne[] = temp.get(j).toString().split("###");
				if(splitOne[1].equalsIgnoreCase("INSERT")){
//					if(opType.isEmpty() || opType.equalsIgnoreCase("u")){
//						opType = opType+"i";
//					}
					
					if(splitOne[0].contains("=")){
						String splitTwo[] = splitOne[0].split("=");
						tempTables.add(splitTwo[0]);
						tempTables.add(splitTwo[1]);
					}else{
						tempTables.add(splitOne[0]);
					}	
				}else if(splitOne[1].equalsIgnoreCase("UPDATE")){
//					if(opType.isEmpty() || opType.equalsIgnoreCase("i")){
//						opType = opType+"u";
//					}
					
					if(splitOne[0].contains("=")){
						String splitTwo[] = splitOne[0].split("=");
						tempTables.add(splitTwo[0]);
						tempTables.add(splitTwo[1]);
					}else{
						tempTables.add(splitOne[0]);
					}
					
				}
				
				
				
				//Now here we though the update and insert pattern matters. But we cannot actually predict it.
//				if(opType.equalsIgnoreCase("iu")){
//					ArrayList tempOne = idnetifyOperationAndBOs(tempTables,allTheBOTables, allTheBOs);
//					if(tempOne.size()>0){
//						createBOs.add(tempOne);
//						}
//					
//					tempTables = new ArrayList();
//					opType = "";
//				}else if(opType.equalsIgnoreCase("ui")){
//					ArrayList tempOne = idnetifyOperationAndBOs(tempTables,allTheBOTables, allTheBOs);
//					if(tempOne.size()>0){
//						createBOs.add(tempOne);
//						}
//					
//					tempTables = new ArrayList();
//					opType = "";
//				}
				
			}
			
			ArrayList tempOne = idnetifyOperationAndBOs(tempTables,allTheBOTables, allTheBOs);
			if(tempOne.size()>1){
				createBOs.add(tempOne);
				}
				
			
		
		}
		
		
		
		for(int i = 0;  i < exclusivelyContained.size(); i++){
			createBOs.remove(exclusivelyContained.get(i));
		}
		
		
		return createBOs;
		
		
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
	
	
	
	

}
