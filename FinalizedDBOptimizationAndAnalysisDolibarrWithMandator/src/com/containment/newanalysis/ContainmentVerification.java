package com.containment.newanalysis;

import java.util.ArrayList;
import java.util.List;

public class ContainmentVerification {
	
	
	/*
	 * This is the class that e use to verify whether ther is a contianment relationship between the business object. 
	 * If there is a possiblity of having a containment relationship then we return the related business objects with the
	 * detials
	 * */
	static List<List<String>> allBoClusters = new ArrayList<List<String>>(); //the table clusters based on same BO relationships Only with insert and delete
	static List<List<String>> allOPClusters = new ArrayList<List<String>>(); //the operations related to the clusters
	
	
	static List<List<String>> allBoClustersWithAll = new ArrayList<List<String>>(); //the table clusters based on same BO relationships
	static List<List<String>> allOPClustersWithAll = new ArrayList<List<String>>(); //the operations related to the clusters

	
	/*
	 * The mehtod use to analyse the containment relationship
	 * List<List<String>>finalClusters - the BOs with their related tables
	 * List<List<String>> allNormalKeytablesDetials - operations related differnt tables with table names
	 * List<List<String>> foriegnKeys  - foreign key relationships betwwen diferrent tables in differnt BOs with the operations.
	 * */
	public static void containmentVerifiactionAnalysis(List<List<String>>finalClusters, List<List<String>> allNormalKeytablesDetials,List<List<String>> foriegnKeys ){
		
		clusterTheBoRelationships(finalClusters, foriegnKeys);
		clusteringAllBoRelationships(finalClusters, foriegnKeys);
		
		
		for(int i = 0; i < allBoClusters.size(); i++){
			
			if(allOPClusters.get(i).contains("INSERT") && allOPClusters.get(i).contains("DELETE")){
				//In this place we have to check whether there are two tables which have both create (Insert) and delete relationshps
				//However futher anlaysis should be conducted to make sure that there is one BO which does not have other
				//insert or delete relationship with another BO
				System.out.println("########################Exclusive Contianment############################");
				ExclusiveOrInclusiveVerification.exclusiveAndInclusiveAnalysis(allBoClusters, allOPClusters, allBoClustersWithAll, allOPClustersWithAll, allNormalKeytablesDetials, i);
				System.out.println("########################Exclusive Contianment############################ \n");
			}else if(allOPClusters.get(i).contains("INSERT")){
				//The aggregation analysis
				System.out.println("########################Inclusive Contianment############################");
				AggregationAnalysis.aggregationAnalysis(allBoClusters, allOPClusters, allBoClustersWithAll, allOPClustersWithAll, allNormalKeytablesDetials, i);
				System.out.println("########################Inclusive Contianment############################ \n");
			}
			
		}
		
		
		//Now here onwards we have to evaluate the association relationship. For this only we will use the
		//allBoClustersWithAll, allOPClustersWithAll, allNormalKeytablesDetials details.
		System.out.println("########################Association############################");
		AssociationAnalysis.associationAnalysis(allBoClustersWithAll, allOPClustersWithAll, allNormalKeytablesDetials);
		System.out.println("########################Association############################ \n");
		
	}
	
	/*
	 * There can be BO relationships between the same BOS because there can be multiple tables inside a single BO.
	 * As such we classify the relationships into groups.
	 */	 
	public static void clusterTheBoRelationships(List<List<String>>finalClusters, List<List<String>> foriegnKeys){
		
		ArrayList theTablesRelated = (ArrayList)foriegnKeys.get(0);//This is to extract the tables with foriegn key relationships
		ArrayList theOperationsRelated = (ArrayList)foriegnKeys.get(1);//This is to extract the operations which are related
		
		ArrayList tempTables = new ArrayList();
		ArrayList tempOperations = new ArrayList();
		for(int i = 0; i < theTablesRelated.size(); i++){
			
			if(theOperationsRelated.get(i).toString().equalsIgnoreCase("INSERT") || theOperationsRelated.get(i).toString().equalsIgnoreCase("DELETE") ){
				tempTables.add(theTablesRelated.get(i));
				tempOperations.add(theOperationsRelated.get(i));
			}
			
		}
		
		theTablesRelated = tempTables;
		theOperationsRelated = tempOperations;
		
		
		
		
		
		
		
		
		
		ArrayList boOneTables = new ArrayList();//ArrayList contains the tabels related to BO one
		ArrayList boTwoTables = new ArrayList();//ArrayList contains the tabels related to BO two
		
		//Now we try to catergorise the table relationships based on the BOs which are related.
		while(theTablesRelated.size() > 0){
			ArrayList boclustes =  new ArrayList();//This is to identify the foriegnkey relationships related to the same BO
			ArrayList operations =  new ArrayList();//This is to identify the operations related to each foriegn key relationship
			
			ArrayList temp = theTablesRelated;
			ArrayList tempTwo = theOperationsRelated;
			
			String splitted[] = theTablesRelated.get(0).toString().split("=");
			
			boclustes.add(theTablesRelated.get(0));
			operations.add(theOperationsRelated.get(0));
			
			for(int i = 0; i < finalClusters.size(); i++){
				ArrayList temp1 = (ArrayList)finalClusters.get(i);
				if(temp1.contains(splitted[0])){
					boOneTables = temp1;
				}else if(temp1.contains(splitted[1])){
					boTwoTables = temp1;
				}
				
			}
			
			
			for(int i=1; i < theTablesRelated.size(); i++){
				String splitted1[] = theTablesRelated.get(i).toString().split("=");
				
				if(boOneTables.contains(splitted1[0]) &&  boTwoTables.contains(splitted1[1])){
					boclustes.add(theTablesRelated.get(i));
					operations.add(theOperationsRelated.get(i));
					theOperationsRelated.remove((int)theTablesRelated.indexOf(theTablesRelated.get(i)));
					theTablesRelated.remove(theTablesRelated.get(i));
					
					
				}else if(boOneTables.contains(splitted1[1]) &&  boTwoTables.contains(splitted1[0])){
					boclustes.add(theTablesRelated.get(i));
					operations.add(theOperationsRelated.get(i));
					theOperationsRelated.remove((int)theTablesRelated.indexOf(theTablesRelated.get(i)));
					theTablesRelated.remove(theTablesRelated.get(i));
					
				}
			}
			
			theTablesRelated.remove(0);
			theOperationsRelated.remove(0);
			
			
			allBoClusters.add(boclustes);
			allOPClusters.add(operations);
			
		}
		
		System.out.println("Finish");
		
	}
	
	
	
	
	
	/*
	 * This method is to categorize the BOS based on all the operations happen and the relationship with the BOs
	 * */
	public static void clusteringAllBoRelationships(List<List<String>>finalClusters, List<List<String>> foriegnKeys){
		ArrayList theTablesRelated = (ArrayList)foriegnKeys.get(0);//This is to extract the tables with foriegn key relationships
		ArrayList theOperationsRelated = (ArrayList)foriegnKeys.get(1);//This is to extract the operations which are related
		
		
		ArrayList boOneTables = new ArrayList();//ArrayList contains the tabels related to BO one
		ArrayList boTwoTables = new ArrayList();//ArrayList contains the tabels related to BO two
		
		//Now we try to catergorise the table relationships based on the BOs which are related.
		while(theTablesRelated.size() > 0){
			ArrayList boclustes =  new ArrayList();//This is to identify the foriegnkey relationships related to the same BO
			ArrayList operations =  new ArrayList();//This is to identify the operations related to each foriegn key relationship
			
			ArrayList temp = theTablesRelated;
			ArrayList tempTwo = theOperationsRelated;
			
			String splitted[] = theTablesRelated.get(0).toString().split("=");
			
			boclustes.add(theTablesRelated.get(0));
			operations.add(theOperationsRelated.get(0));
			
			for(int i = 0; i < finalClusters.size(); i++){
				ArrayList temp1 = (ArrayList)finalClusters.get(i);
				if(temp1.contains(splitted[0])){
					boOneTables = temp1;
				}else if(temp1.contains(splitted[1])){
					boTwoTables = temp1;
				}
				
			}
			
			
			for(int i=1; i < theTablesRelated.size(); i++){
				String splitted1[] = theTablesRelated.get(i).toString().split("=");
				
				if(boOneTables.contains(splitted1[0]) &&  boTwoTables.contains(splitted1[1])){
					boclustes.add(theTablesRelated.get(i));
					operations.add(theOperationsRelated.get(i));
					theOperationsRelated.remove((int)theTablesRelated.indexOf(theTablesRelated.get(i)));
					theTablesRelated.remove(theTablesRelated.get(i));
					
					
				}else if(boOneTables.contains(splitted1[1]) &&  boTwoTables.contains(splitted1[0])){
					boclustes.add(theTablesRelated.get(i));
					operations.add(theOperationsRelated.get(i));
					theOperationsRelated.remove((int)theTablesRelated.indexOf(theTablesRelated.get(i)));
					theTablesRelated.remove(theTablesRelated.get(i));
					
				}
			}
			
			theTablesRelated.remove(0);
			theOperationsRelated.remove(0);
			
			
			allBoClustersWithAll.add(boclustes);
			allOPClustersWithAll.add(operations);
			
			
		}
		System.out.println("All BO with Operations");
		
	}
	
}
