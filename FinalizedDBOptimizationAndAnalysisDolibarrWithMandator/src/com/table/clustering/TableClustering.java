package com.table.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java_cup.Main;



public class TableClustering {

	//here we do the clustering for the databse tabels. The idea is to identify the business objects based on the table clustering.
	//In order to achieve this we will follow the following steps.
	//1. Based on the Entropy value we will decide a Main table for each cluster
	//2. Then we will calculate the similarity between that table and the rest of the tables one by one based on qgram values
	//3. In each of these claculations the table will be check tho see if it is in other cluasters also. 
	//As such it will be checked with the  qgram values of it also. 
	//4. Tables which provides the loves q-gram will be the cluster which that particular table will be belongs to.
	
	//Contains the main table of each cluster
	static String [] mainTables;
	static float[] mainEntropys;
			
			
	//The reclustered ggroup of tables in order to create BO is listed here.
	//static List<List<String>> reclusteredGroups = new ArrayList<List<String>>();;
	
	public static List<List<String>> tableClustering(List<List<String>> clusterGroups){
		//Calculated entropy values for the tables
		ArrayList entropy = EntropyCalculation.calculatingTheEntropy();
		//Table names list with all the table names
		ArrayList allTableNames = EntropyCalculation.getDatabseTabelNames();
		//All the tables which got clustered
		List<List<String>> clusterignGroups = clusterGroups;
		
		mainTables  = new String[clusterignGroups.size()];
		mainEntropys = new float[clusterignGroups.size()];
		
		for(int i=0;i<clusterignGroups.size();i++){
			ArrayList tempGroup = (ArrayList) clusterignGroups.get(i);
			float entropyTake = 0.0f;
			String nameOfTable = "";
			
			for(int j=0; j<tempGroup.size(); j++){
				if(j==0){
					nameOfTable = (String) tempGroup.get(j);
					int index = allTableNames.indexOf(nameOfTable);
					entropyTake = (float) entropy.get(allTableNames.indexOf(nameOfTable));
				}else{
					if((float) entropy.get(allTableNames.indexOf(nameOfTable)) > entropyTake){
						nameOfTable = (String) tempGroup.get(j);
						entropyTake = (float) entropy.get(allTableNames.indexOf(nameOfTable));
					}
					
				}
				
			}
			
			mainTables[i] = nameOfTable;
			mainEntropys[i] = entropyTake;
				
			
		}
		
		return reclusteringTheTablesToGenerateBO(clusterGroups);
		
		
	}
	
	
	
	
	public static List<List<String>> reclusteringTheTablesToGenerateBO(List<List<String>> clusterGroups){
		
		List<List<String>> reClusterGroups = new ArrayList<List<String>>();
		ArrayList tablesGotAdded = new ArrayList();
		
		
		//If there are two clusters with the same parent table,it is better to combine them to a single cluster
		for(int i=0;i<clusterGroups.size();i++){
			String mainTableSelected = mainTables[i];
			int totalNoOfNeighbours = 0;
			
			ArrayList tempOne = (ArrayList) clusterGroups.get(i);
			for(int j=0;j<mainTables.length;j++){
				
				if(i!=j && mainTableSelected.equalsIgnoreCase(mainTables[j]) && !tablesGotAdded.contains(j)){
					tablesGotAdded.add(j);
					ArrayList tempTwo = (ArrayList) clusterGroups.get(j);
					
					for(int k=0;k<tempTwo.size();k++){
						if(!tempOne.contains(tempTwo.get(k))){
							tempOne.add(tempTwo.get(k));
						}
						
					}
					
					//clusterGroups.remove(j);
				}
				
				
			}
			 
			 
			reClusterGroups.add(tempOne);
			 
		}
		
		
		
		//---------------Removed Secondly
//		List<List<Integer>> placesToAddSubGroups = new ArrayList<List<Integer>>(); 
//		ArrayList subgroupNo = new ArrayList();
//		ArrayList tableNames = EntropyCalculation.getDatabseTabelNames();
//		ArrayList entropyValue = EntropyCalculation.calculatingTheEntropy();
//		
//		
//		
//		for(int i=0;i<reClusterGroups.size();i++){
//			ArrayList temArrayOne = (ArrayList) reClusterGroups.get(i);
//			float entropyValuesOfParentTable = 0;
//			ArrayList placesNeededToGetAdded = new ArrayList();
//			
//			for(int j=0;j<temArrayOne.size();j++){
//				if(entropyValuesOfParentTable < (float)entropyValue.get(tableNames.indexOf(temArrayOne.get(j))) ){
//					entropyValuesOfParentTable = (float)entropyValue.get(tableNames.indexOf(temArrayOne.get(j)));
//				}
//			}
//			
//			int contained = 0;
//			for(int j=0; j<reClusterGroups.size();j++){
//				
//				if(i != j){
//					ArrayList temArrayTwo = (ArrayList) reClusterGroups.get(j);
//					float entropyValuesOfSubTable = 0;
//					
//					for(int k=0;k<temArrayTwo.size();k++){
//						if(entropyValuesOfSubTable < (float)entropyValue.get(tableNames.indexOf(temArrayTwo.get(k))) ){
//							entropyValuesOfSubTable = (float)entropyValue.get(tableNames.indexOf(temArrayTwo.get(k)));
//						}
//				}
//					
//					int similarityCount = 0;
//					for(int x =0;x<temArrayTwo.size();x++){
//							if(temArrayOne.contains(temArrayTwo.get(x))){
//								similarityCount++;
//							}
//						
//						}
//					
//					if((float)similarityCount/temArrayTwo.size() > 0.8 && entropyValuesOfParentTable>entropyValuesOfSubTable){
//						placesNeededToGetAdded.add(j);
//						contained =1;
//						
//					}
//					}
//				
//			}
//			
//			if(contained == 1){
//				subgroupNo.add(i);
//				placesToAddSubGroups.add(placesNeededToGetAdded);
//			}
//			
//			
//		}
//		//---------------Removed Secondly
		
		
		
		//Now we have to do the reclustering again, if the same parent table is a child table of another cluster.
		//And the entropy of that parent table is greater than that of of the second one. 
		//Then we remove all the values related to the cluster which have the sub parent.
		List<List<String>> reClusterGroupsAgain = new ArrayList<List<String>>();
		ArrayList positionstoToRemove = new ArrayList();
		List tablenameList = Arrays.asList(mainTables);
		
		for(int i=0;i<reClusterGroups.size();i++){
			ArrayList tempOne = (ArrayList) reClusterGroups.get(i);
			
			for(int j=0;j<reClusterGroups.size();j++){
				ArrayList tempTwo = (ArrayList) reClusterGroups.get(j);

				if(i!=j && tempOne.contains(tempTwo.get(0)) && (mainEntropys[tablenameList.indexOf(tempOne.get(0))] > mainEntropys[tablenameList.indexOf(tempTwo.get(0))]) ){
					
					if(!positionstoToRemove.contains(j)){
						positionstoToRemove.add(j);
					}
					
					
//					for(int k =0 ; k<tempTwo.size();k++){
//						if(!tempOne.contains(tempTwo.get(k))){
//							tempOne.add(tempTwo.get(k));
//						}
//					}
					
				}
				
			}
			
		}
		
		
		for(int i=0;i<reClusterGroups.size();i++){
			if(!positionstoToRemove.contains(i)){
				reClusterGroupsAgain.add(reClusterGroups.get(i));
			}
			
			
		}
		
		
		
		
		
		//---------------Removed Secondly
//		ArrayList allGraphsGotDuplicated = new ArrayList();
//		for(int i=0;i<placesToAddSubGroups.size();i++){
//			ArrayList tempArray = (ArrayList) placesToAddSubGroups.get(i);
//			for(int j=0;j<tempArray.size();j++){
//				allGraphsGotDuplicated.add(tempArray.get(j));
//			}
//					
//		}
//		
//		List<List<String>> novelReClusteredGroup = new ArrayList<List<String>>();
//		
//		for(int i=0;i<reClusterGroups.size();i++){
//			if(!allGraphsGotDuplicated.contains(i) && subgroupNo.contains(i)){
//				ArrayList tempArray = (ArrayList) reClusterGroups.get(i);
//				
//				ArrayList positionToAdd  = (ArrayList) placesToAddSubGroups.get(subgroupNo.indexOf(i));
//				for(int j=0;j<positionToAdd.size();j++){
//					ArrayList tempArrayTwo = (ArrayList) reClusterGroups.get((int)positionToAdd.get(j));
//					
//					for(int x=0;x<tempArrayTwo.size();x++){
//						if(!tempArray.contains(tempArrayTwo.get(x))){
//							tempArray.add(tempArrayTwo.get(x));
//						}
//						
//					}
//				}
//				
//				novelReClusteredGroup.add(tempArray);
//			}else if(!allGraphsGotDuplicated.contains(i)){
//				novelReClusteredGroup.add(reClusterGroups.get(i));
//			}
//			
//		}
		//---------------Removed Secondly
		
		
		System.out.println("Test");
		
		ArrayList singelTablesToRemove = new ArrayList();
		//Removeing Clusters with only one table and it is in another cluster
		for(int i=0;i<reClusterGroupsAgain.size();i++){
			ArrayList tempOne = (ArrayList) reClusterGroupsAgain.get(i);
			 if(tempOne.size() == 1){
				 for(int j=0;j<reClusterGroupsAgain.size();j++){
					 ArrayList tempTwo = (ArrayList) reClusterGroupsAgain.get(j);
					 if(i != j && tempTwo.contains(tempOne.get(0))){
						 singelTablesToRemove.add(i);
					 }
				 }
			 }
			
		}
		
		
		List<List<String>> reClusterGroupsAgainTemp = new ArrayList<List<String>>();
		for(int i=0;i<reClusterGroupsAgain.size();i++){
			if(!singelTablesToRemove.contains(i)){
				reClusterGroupsAgainTemp.add(reClusterGroupsAgain.get(i));
			}
			
		}
		
		//Now we have to remove the subset of the lists in reClusterGroupAgain cluster, inorder to remove the redundencies.
		
		
		
		
		return reClusterGroupsAgainTemp;
	}
	
	
	
	
	
	
	//This is not necessary. We change the code to get the median And based on that calculate the clusters
	//In this step we check the number of realtionships each table have with its parent table.
	//If it is greater than a given value it would be calculated as belong to that particular group.
//	public static List<List<String>> reclusteringTheTablesToGenerateBO(ArrayList nameOfTheTables, List<List<Integer>>  clusteringTableRlationships, ArrayList medianValues){
//		
//		for(int i=0;i<mainTables.length;i++){
//			String mainTableName  = mainTables[i];
//			int positionOfTheTable =  nameOfTheTables.indexOf(mainTableName);
//			
//			ArrayList<Integer> relationshipValuesOfMainTable = (ArrayList<Integer>) clusteringTableRlationships.get(positionOfTheTable);
//			float medianValueOfThatRow  = (float) medianValues.get(positionOfTheTable);
//			
//			ArrayList newClusterOfTables = new ArrayList();
//			newClusterOfTables.add(mainTableName);
//			
//			for(int j=0;j<relationshipValuesOfMainTable.size();j++){
//				if(relationshipValuesOfMainTable.get(j) > medianValueOfThatRow){
//					newClusterOfTables.add(nameOfTheTables.get(j));
//				}
//			}
//			
//			reclusteredGroups.add(newClusterOfTables);
//			
//			
//		}
//		
//		return reclusteredGroups;
//		
//	}
	
	
	
	public static void clusterGroupComparison(){
		
	}
	
	
	
}
