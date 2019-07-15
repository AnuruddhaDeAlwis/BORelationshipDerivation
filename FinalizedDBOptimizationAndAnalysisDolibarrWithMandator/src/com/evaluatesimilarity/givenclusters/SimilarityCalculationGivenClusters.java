package com.evaluatesimilarity.givenclusters;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedListener;

import com.data.queries.HandlingQueries;
import com.jaccard.calculation.Cosine;
import com.jaccard.calculation.Jaccard;
import com.qgram.calculation.QGram;
import com.table.clustering.EntropyCalculation;

//When the identified table clusters were given to in here we try to identify tables which are reperted in two clusters
//Then we try to figure out to which cluster that particular table should be belongs to.

public class SimilarityCalculationGivenClusters {
	public static ArrayList nameOfTheTableGetRepeated = new ArrayList();
	public static List<List<Integer>> clusterNoThatEachTableGetReperated = new ArrayList<List<Integer>>();
	
	
	public static void processingTableSimilarity(List<List<String>> reClusteredGroups){
		identifyRepeatingTables(reClusteredGroups);
		
	}
	

	
	//This is just to identify the repeating tables and to generate the following
	//1. nameOfTheTableGetRepeated - the table which get repeated inmultiple clusters.
	//2. clusterNoThatEachTableGetReperated - identify the clusters on which each table gets repeated.
	public static List<List<String>> identifyRepeatingTables(List<List<String>> reClusteredGroups){

		for(int i=0; i<reClusteredGroups.size();i++){
			
			ArrayList tempOne = (ArrayList) reClusteredGroups.get(i);
			
			for(int j=0; j<tempOne.size();j++){
				int contained = 0;
				ArrayList repeatingClusters = new ArrayList();
				
				if(!nameOfTheTableGetRepeated.contains(tempOne.get(j))){
					for(int k=0;k<reClusteredGroups.size();k++){
						ArrayList tempTwo = (ArrayList) reClusteredGroups.get(k);
						if(tempTwo.contains(tempOne.get(j)) && k!=i){
							if(!repeatingClusters.contains(k)){
								repeatingClusters.add(k);
							}
							
							if(!nameOfTheTableGetRepeated.contains(tempOne.get(j))){
								nameOfTheTableGetRepeated.add(tempOne.get(j));
							}
							
							contained = 1;
						}
						
					}
					
					if(contained == 1){
						repeatingClusters.add(i);
						clusterNoThatEachTableGetReperated.add(repeatingClusters);
					}
				}
				
			}
			
			
			
		}
	
		System.out.println("Test");
		
		return IdentifyTheClusterEachTableBelongs(reClusteredGroups);
		
	}
	
	
	
	//In the above method we have identify the tables that are in multiple clusters and the clusters which contain them
	//The next step is to identify the cluster which each table should be belong to. 
	//In order to achieve this we will check the similarity values of the each table with the main table in each cluster.
	public static List<List<String>> IdentifyTheClusterEachTableBelongs(List<List<String>> reClusteredGroups){
		 ArrayList clusterNumberEachTableBelongs = new ArrayList();
		 ArrayList mainTableInEachCluster = IdentifytheMaintableInEachCluster(reClusteredGroups);
		 
		
		for(int i=0; i<nameOfTheTableGetRepeated.size();i++){
		    int clusterNoofTable = 0;
			float cosineValue = 0.0f;
		    float jaccardValue = 0.0f;
		    float qgramValue = 0.0f;
			
			ArrayList mainTablesRelated = (ArrayList) clusterNoThatEachTableGetReperated.get(i);
			String dataOfMainTable = HandlingQueries.getAllValuesOfTabel(nameOfTheTableGetRepeated.get(i).toString());
			dataOfMainTable = dataOfMainTable.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\'", "").replaceAll("NULL", "");
			for(int j=0;j<mainTablesRelated.size();j++){
				String dataOfSubTable = HandlingQueries.getAllValuesOfTabel(mainTableInEachCluster.get((int)mainTablesRelated.get(j)).toString());
				dataOfSubTable = dataOfSubTable.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\'", "").replaceAll("NULL", "");
				
				QGram qgram = new QGram(5);
				if(qgramValue == 0){
					qgramValue = (float) qgram.distance(dataOfMainTable, dataOfSubTable);
					clusterNoofTable = (int)mainTablesRelated.get(j);
				}else if(qgramValue > (float) qgram.distance(dataOfMainTable, dataOfSubTable)){
					qgramValue = (float) qgram.distance(dataOfMainTable, dataOfSubTable);
					clusterNoofTable = (int)mainTablesRelated.get(j);
				}
				
//				Jaccard jac = new Jaccard(5);
//				float jac1 = (float) jac.distance(dataOfMainTable, dataOfSubTable);
				
				
//				Cosine cos = new Cosine(5);
//				float cos1 = (float) cos.distance(dataOfMainTable, dataOfSubTable);
				
				
//				if(jac1 > jaccardValue){
//					jaccardValue = jac1;
//					//cosineValue = cos1;
//					clusterNoofTable = (int)mainTablesRelated.get(j);
//					
//				}
				
				
			}
			
			clusterNumberEachTableBelongs.add(clusterNoofTable);
			
			
		}
		
		
		
		
		return filterTheTableInTheClusters(reClusteredGroups, clusterNumberEachTableBelongs);
		
		
	}
	
	
	
	
	public static List<List<String>> filterTheTableInTheClusters(List<List<String>> reClusteredGroups, ArrayList clusterNumberEachTableBelongs){
		
		for(int i=0; i<nameOfTheTableGetRepeated.size();i++){
			String tableNameGetRepeated = nameOfTheTableGetRepeated.get(i).toString();
			
			for(int j=0;j<reClusteredGroups.size();j++){
				ArrayList tempArray = (ArrayList) reClusteredGroups.get(j);
				
				if(tempArray.contains(tableNameGetRepeated) && j!= (int)clusterNumberEachTableBelongs.get(i) ){
					tempArray.remove(tableNameGetRepeated);
					reClusteredGroups.set(j, tempArray);
				}
				
			}
			
			
		}
		
		
		List<List<String>> reClusteredGroupsTemp = new ArrayList<List<String>>();
		
		for(int i=0;i<reClusteredGroups.size();i++){
			
			if(reClusteredGroups.get(i).size() > 0){
				reClusteredGroupsTemp.add(reClusteredGroups.get(i));
			}
			
		}
		
		
		return reClusteredGroupsTemp;
	}
	
	
	
	
	
	
	
	
	
	
	//We have to identify the main table in each cluster
	public static ArrayList IdentifytheMaintableInEachCluster(List<List<String>> reClusteredGroups){
		//Calculated entropy values for the tables
		ArrayList entropy = EntropyCalculation.calculatingTheEntropy();
		//Table names list with all the table names
		ArrayList allTableNames = EntropyCalculation.getDatabseTabelNames();
		
		//The Main Table ArrayList
		ArrayList mainTables = new ArrayList();
		
		for(int i=0; i<reClusteredGroups.size() ; i++){
			String mainTablename = "";
			ArrayList tempCluster = (ArrayList)reClusteredGroups.get(i);
			
			for(int j=0;j<tempCluster.size();j++){
				if(j==0){
					mainTablename = tempCluster.get(j).toString();
				}else{
					if((float)entropy.get(allTableNames.indexOf(tempCluster.get(j).toString())) > (float)entropy.get(allTableNames.indexOf(mainTablename))){
						mainTablename = tempCluster.get(j).toString();
					}
				}
			}
			
			mainTables.add(mainTablename);
		}
		
		return mainTables;
	}
	
	
	

}
