package com.containment.analysis;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.NoType;

public class EvalauateExclusiveContainment {

	//In this class we evalaute the exclusive contianment relationship.
	//1. First we check whether there any business objects which have only one relationship with other business object
	//2. If so then we have to check whether there is an independent life cycle for the identified business objects.
	
	/*
	 * noOfBos- Total number of business objects identified
	 * boRelationships - Idnefitidied relationships between the busness objects
	 * boClusters - business object clusters
	 * normalTablesDetails - identified tables which have independent life cycles (with out foriegnkey relationships)
	 */
	public static ArrayList evaluateExclusiveContainment(int noOfBos, ArrayList boRelationships, List<List<String>> boClusters, List<List<String>> normalTablesDetails){
		
		List<List<String>> boRelathionshipsIdentified = new ArrayList<List<String>>();
		
		for(int i=0;i<noOfBos;i++){
			ArrayList temp = new ArrayList();
			boRelathionshipsIdentified.add(temp);
		}
		
		
		for(int i=0; i<boRelationships.size();i++){
			String splitOne[] = boRelationships.get(i).toString().split("-");
			
			int one =Integer.parseInt(splitOne[0].toString());
			int two =Integer.parseInt(splitOne[1].toString());
			
			ArrayList temp1 = (ArrayList)boRelathionshipsIdentified.get(one);
			ArrayList temp2 = (ArrayList)boRelathionshipsIdentified.get(two);
			
			temp1.add(two);
			temp2.add(one);
			
			boRelathionshipsIdentified.set(one, temp1);
			boRelathionshipsIdentified.set(two, temp2);
		}
		
		return processingToIdentifyExclusivelyContainedObjects(boRelathionshipsIdentified, boClusters, normalTablesDetails);
	}
	
	
	
	public static ArrayList processingToIdentifyExclusivelyContainedObjects(List<List<String>> boRelathionshipsIdentified, List<List<String>> boClusters, List<List<String>> normalTablesDetails){
		
		ArrayList allNormalTable = (ArrayList)normalTablesDetails.get(0);//Normal tables names
		ArrayList operationRelatedToNormalTables = (ArrayList)normalTablesDetails.get(1);// The operations performmed on the tables
		
		ArrayList bosWithExclusiveConatiment = new ArrayList();//This is to store the exclusive containe business objects
		
		for(int i=0;i<boRelathionshipsIdentified.size();i++){
			//In this we check whether we have busisness objects which have only one relationshps with other BOs
			//If we identify them those are possible candidates for the exclusive containment
			int exclusivelyContianed = 1;
			String exclusivelyRelatedBOs = "Null";
			
			if(((ArrayList)boRelathionshipsIdentified.get(i)).size() == 1){
				ArrayList temp = (ArrayList)boRelathionshipsIdentified.get(i);
				int tempInt = (int)temp.get(0);
				
				ArrayList tablesIntheBO = 	(ArrayList)boClusters.get(i);
				
				
				//if tempInt boRelationship has more than one table. Then it should be the main BO in the exclusive containment
				//relationships
				if(((ArrayList)boRelathionshipsIdentified.get(tempInt)).size() > 1){
					//Here we only have to evalaute whether there are individual life cycle for the tables in the identified business
					//object. If it contains individual life cycles then it is not exclusive containment
					for(int j=0;j<tablesIntheBO.size();j++){
						if(allNormalTable.contains(tablesIntheBO.get(j))){
							//This means that there are independent tables. However, if still the operation perform on it is SELECT or 
							//UPDATE then it can still be exclusive contianment. Because it will be destroyed and created only with 
							//the main object.
							for(int k=0;k<allNormalTable.size();k++){
								if(allNormalTable.get(k).toString().equalsIgnoreCase(tablesIntheBO.get(j).toString())){
									String theOperation = operationRelatedToNormalTables.get(k).toString();
									
									if(theOperation.equalsIgnoreCase("DELETE") || theOperation.equalsIgnoreCase("INSERT")){
										exclusivelyContianed = 0;
									}
								}
								
							}
						}
						
						
					}
					
					
					if(exclusivelyContianed == 1){
						//Which means that there are no DELETE or INSERT operations related to the tables in the BO
						//tempInt (The main BO) and i (exclusively contained BO)
						exclusivelyRelatedBOs = tempInt+"-"+i;
						System.out.println("The Value is: "+exclusivelyRelatedBOs);
					}
					
					
				}else{
					//This is for situations where both of the objects identified have single relationship the other business object
					//Such situation we have to identify which one in the exclusively contained bo and exclusively container
					ArrayList firstBO = (ArrayList)boClusters.get(i);//The first BO object
					ArrayList secondBO = (ArrayList)boClusters.get(tempInt);//The second BO object
					
					int firstBoContainesAllOperations = 0; //To evaluate whether the first BO has all four CRUD operations
					int secondBoContainesAllOperations = 0; //To evaluate whether the second BO has all four CRUD operations
					
					//Check the first BO 
					for(int j=0;j<firstBO.size();j++){
						if(allNormalTable.contains(firstBO.get(j))){
							for(int k=0;k<allNormalTable.size();k++){
								if(allNormalTable.get(k).toString().equalsIgnoreCase(firstBO.get(j).toString())){
									String theOperation = operationRelatedToNormalTables.get(k).toString();
									
									if(theOperation.equalsIgnoreCase("DELETE") || theOperation.equalsIgnoreCase("INSERT")){
										firstBoContainesAllOperations = 1;
									}
								}
							}
						
						}
					}
					
					
					//Check the second BO 
					for(int j=0;j<secondBO.size();j++){
						if(allNormalTable.contains(secondBO.get(j))){
							for(int k=0;k<allNormalTable.size();k++){
								if(allNormalTable.get(k).toString().equalsIgnoreCase(secondBO.get(j).toString())){
									String theOperation = operationRelatedToNormalTables.get(k).toString();
									
									if(theOperation.equalsIgnoreCase("DELETE") || theOperation.equalsIgnoreCase("INSERT")){
										secondBoContainesAllOperations = 1;
									}
								}
								
							}
						}
					}
					
					
					
					if(firstBoContainesAllOperations == 1 && secondBoContainesAllOperations == 0){
						exclusivelyRelatedBOs = i+"-"+tempInt;
					}else if(firstBoContainesAllOperations == 0 && secondBoContainesAllOperations == 1){
						exclusivelyRelatedBOs = tempInt+"-"+i;
					}
					
				}
				
				
			}
			
			
			if(!bosWithExclusiveConatiment.contains(exclusivelyRelatedBOs) && !exclusivelyRelatedBOs.equalsIgnoreCase("Null")){
				bosWithExclusiveConatiment.add(exclusivelyRelatedBOs);
				System.out.println("Inside The Addition");
			}
			
		
		}
		
		return bosWithExclusiveConatiment;
		
	}
	
}
