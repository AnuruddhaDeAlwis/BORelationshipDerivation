package com.containment.analysis;

import java.util.ArrayList;
import java.util.List;

public class EvaluatingInclusiveContainment {
	
	//This is the class which we use to basically evaluate and identify the inclusive containment operations.
		//1. First we check whether there any business objects which have only one relationship with other business object
		//2. If so then we have to check whether there is an independent life cycle for the identified business objects.
		
		/*
		 * noOfBos- Total number of business objects identified
		 * boRelationships - Idnefitidied relationships between the busness objects
		 * boClusters - business object clusters
		 * normalTablesDetails - identified tables which have independent life cycles (with out foriegnkey relationships)
		 */
		public static void evalauteInclusiveContainment(int noOfBos, ArrayList boRelationships, List<List<String>> boClusters, List<List<String>> normalTablesDetails, 
				List<List<String>> fkeysAndOperations){
			
			
			//boRelathionshipsIdentified - contains the summarized overview of the business object relationships
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
			
			List<List<String>> allRelationshipsProcessed = processingToIdentifyInclusivelyContainedObjects(boRelathionshipsIdentified, boClusters, normalTablesDetails,fkeysAndOperations);
			finalizingTheRelationships(boRelathionshipsIdentified, allRelationshipsProcessed);
		
		}
		
		
		
		
		
		
		public static List<List<String>> processingToIdentifyInclusivelyContainedObjects(List<List<String>> boRelathionshipsIdentified, List<List<String>> boClusters, List<List<String>> normalTablesDetails,
				List<List<String>> fkeysAndOperations){
			
			List<List<String>> allRelationshipsProcessed = new ArrayList<List<String>>();
			for(int i=0;i<boRelathionshipsIdentified.size();i++){
				ArrayList virtualArray = new ArrayList();
				allRelationshipsProcessed.add(virtualArray);
			}
			
			ArrayList allNormalTable = (ArrayList)normalTablesDetails.get(0);//Normal tables names
			ArrayList operationRelatedToNormalTables = (ArrayList)normalTablesDetails.get(1);// The operations performmed on the tables
			
			ArrayList bosWithInclusiveConatiment = new ArrayList();//This is to store the inclusive containe business objects
			
			for(int i=0;i<boRelathionshipsIdentified.size();i++){
				//In this we check whether we have busisness objects which have only one relationshps with other BOs
				//If we identify them those are possible candidates for the exclusive containment
				int inclusivelyContianed = 1;
				String inclusivelyRelatedBOs = "Null";
				
				ArrayList temp = (ArrayList)boRelathionshipsIdentified.get(i);
				
				
				//Tables in first BO
				ArrayList tablesIntheParentBO = (ArrayList)boClusters.get(i);
				
				//First we check whether there are independent insert and delete operations related to the tablesIntheParentBO
				for(int j=0;j<tablesIntheParentBO.size();j++){
					//If there is independent delete or inset operation then the parent table does not
					//have a inclusive contain relationship
					for(int k=0;k<allNormalTable.size();k++){
						if(allNormalTable.get(k).toString().equalsIgnoreCase(tablesIntheParentBO.get(j).toString())){
							String theOperation = operationRelatedToNormalTables.get(k).toString();
							
							if(theOperation.equalsIgnoreCase("DELETE") || theOperation.equalsIgnoreCase("INSERT")){
								inclusivelyContianed = 0;
							}
						}
						
					}
				}
				
				//To store the relationship type related to each BO with the Parent BO
				ArrayList relationshipType = new ArrayList();
				
				if(inclusivelyContianed == 1){
					System.out.println("Possibly Inclusively Contained  "+i);
					for(int k=0;k<temp.size();k++){
						ArrayList tablesIntheChildBO = (ArrayList)boClusters.get((int)temp.get(k));
						
						int value = processingTheRelationships(tablesIntheParentBO, tablesIntheChildBO, fkeysAndOperations);
						relationshipType.add(value);
						
						
					}
					
					allRelationshipsProcessed.set(i, relationshipType);
					
					
					
					
				}
				
				
				
				
				
				
			
			}
			
			return allRelationshipsProcessed;
			
			//Now we have to process the all the RelationshipedProcessed and boRelathionshipsIdentified
			
		}
		
		
		
		
		public static int processingTheRelationships(ArrayList parentBO, ArrayList childBO, List<List<String>> fkeysAndOperations){
			
			ArrayList allTheFKeys = (ArrayList)fkeysAndOperations.get(0);
			ArrayList allTheOperations = (ArrayList)fkeysAndOperations.get(1);
			
			ArrayList typesOfOperationsPerformed = new ArrayList(); //To store the type of relationships
			
			for(int i=0;i<parentBO.size();i++){
				//Get each table of the parent business object
				String parentTable = parentBO.get(i).toString();
				
				for(int j=0;j<childBO.size();j++){
					//get each table of the child business object
					String childTable = childBO.get(j).toString();
					
					for(int k=0;k<allTheFKeys.size();k++){
						//check whether there are ny relationships with the ientified parent tabel and the child table
						String foreignKeyRelation = allTheFKeys.get(k).toString();
						if(foreignKeyRelation.contains(parentTable) && foreignKeyRelation.contains(childTable)){
							String operationPerformed = allTheOperations.get(k).toString();
							
							//We have only focused on the Insert and Delete because business objects should be able to update and 
							//read without affecting the related business objects
							if(operationPerformed.equalsIgnoreCase("INSERT") && !typesOfOperationsPerformed.contains("INSERT")){
								typesOfOperationsPerformed.add("INSERT");
							}else if(operationPerformed.equalsIgnoreCase("DELETE") && !typesOfOperationsPerformed.contains("DELETE")){
								typesOfOperationsPerformed.add("DELETE");
							}
//							}else if(operationPerformed.equalsIgnoreCase("SELECT") && !typesOfOperationsPerformed.contains("SELECT")){
//								typesOfOperationsPerformed.add("SELECT");
//							}else if(operationPerformed.equalsIgnoreCase("UPDATE") && !typesOfOperationsPerformed.contains("UPDATE")){
//								typesOfOperationsPerformed.add("UPDATE");
//							}
							
							
						}
					}
							
					
				}
				
			}
			
			
			
			//Analyze and finalize whether there is a inclusive relationship between BOs and what are they
			
				return typesOfOperationsPerformed.size();
			
			
			
		}
		
		
		
		public static void finalizingTheRelationships(List<List<String>> boRelathionshipsIdentified, List<List<String>> allRelationshipsProcessed){
			
			for(int i=0; i<boRelathionshipsIdentified.size();i++){
				ArrayList rpProcessed = (ArrayList)allRelationshipsProcessed.get(i);
				ArrayList boRelations = (ArrayList)boRelathionshipsIdentified.get(i);
				
				if(rpProcessed.contains(2)){
					for(int j=0;j<rpProcessed.size();j++){
						if((int)rpProcessed.get(j) == 2){
							System.out.println("Inclusive Conatinment "+i+"-"+boRelations.get(j).toString());
						}
						
					}
				}else 	if(rpProcessed.contains(1)){
					for(int j=0;j<rpProcessed.size();j++){
						if((int)rpProcessed.get(j) == 1){
							System.out.println("Inclusive Conatinment "+i+"-"+boRelations.get(j).toString());
						}
						
					}
				}
				
				
				
			}
			
			
			
		}
		
		

}
