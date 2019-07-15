package com.containment.newanalysis;

import java.util.ArrayList;
import java.util.List;

public class AssociationAnalysis {
	/*
	 * This class is created to analyse the association relationships between the business objects.
	 * Basically we define an association as a situation where there are BO objects which are related only by the 'SELECT'operation
	 * This means that those BO are not created or deleted together. However, they are used to be selected together.
	 * */
	
	
	/*
	 * This method is repsonsible for analysing the association relationship
	 * allBoClustersWithAll - the table clusters based on same BO relationships
	 * allOPClustersWithAll -  the operations related to the clusters
	 * allNormalKeytablesDetials - normal table operations with the realted information
	 * */
	public static void associationAnalysis(List<List<String>> allBoClustersWithAll, List<List<String>> allOPClustersWithAll,  
			List<List<String>> allNormalKeytablesDetials){
	
		
		List<List<String>> allBoClustersWithSelectOnly = new ArrayList<List<String>>(); //This contains all the BOs which only have select operations
		
		for(int i = 0; i < allOPClustersWithAll.size(); i++){
			
			ArrayList temp = (ArrayList)allOPClustersWithAll.get(i);
			ArrayList temp1 = (ArrayList)allBoClustersWithAll.get(i);
			
			if(temp.contains("INSERT") || temp.contains("Insert") || temp.contains("insert")){
				
			}else if(temp.contains("DELETE") || temp.contains("Delete") || temp.contains("delete")){
				
			}else if(temp.contains("UPDATE") || temp.contains("Update") || temp.contains("update")){
				
			}else{
				allBoClustersWithSelectOnly.add(temp1);
			}
			
		}
		
		
		
		for(int i = 0; i < allBoClustersWithSelectOnly.size(); i++){
			ArrayList temp = (ArrayList)allBoClustersWithSelectOnly.get(i);
			String tablesRelated = "";
			int independency = 0;
			for(int j = 0; j < temp.size(); j++){
				
				String split[] = temp.get(j).toString().split("=");
				tablesRelated = tablesRelated + temp.get(j).toString()+" ";
				
				if(VerifyIndependentSelectOps.selectOperationAnalysis(split[1], split[0], allNormalKeytablesDetials)){
					independency++;
				}
			}
			
			if(independency > 0){
				System.out.println(tablesRelated+" are associated and have independent select operations as such it is okay to separate them"
						+ " into different microservices \n");
			}else{
				System.out.println(tablesRelated+" are associated and do not have independent select operations as such"
						+ " they should be seperated but kept in the same container. \n");
			}
			
		}
		
		
		
		System.out.println("Testing");
		
	}

}
