package com.evaluatesimilarity.givenclusters;

import java.util.ArrayList;
import java.util.List;

import com.data.queries.HandlingQueries;
import com.qgram.calculation.QGram;
import com.table.clustering.EntropyCalculation;

//This calss is responsible for putting te tables which are not in the clusters to the correct clusters.
public class InsertingTablesIntoFilteredClusters {
	
	public static List<List<String>> filteringTablesIntoCluster(List<List<String>>newClusters, ArrayList allTableThatGotProccessed){
		ArrayList entropy = EntropyCalculation.calculatingTheEntropy();//EntropyValues of all the databse tables
		ArrayList allTableNames = EntropyCalculation.getDatabseTabelNames();//Names of the databse tables according to entropy values
		ArrayList mainTables = new ArrayList();
		
		//Removing the tables which have already been processed and in the clusters
		for(int i=0;i<newClusters.size();i++){
			ArrayList tempArray = (ArrayList)newClusters.get(i);
			for(int j=0;j<tempArray.size();j++){
				if(allTableThatGotProccessed.contains(tempArray.get(j))){
					allTableThatGotProccessed.remove(tempArray.get(j));
				}
			}
		}
		
		
		//calculating the maintable in each cluster
		for(int i=0;i<newClusters.size();i++){
			ArrayList tempArray = (ArrayList)newClusters.get(i);
			String mainTable = "";
			float entropyValue = 0.0f;
			for(int j=0;j<tempArray.size();j++){
				if(j == 0){
					entropyValue = (float)entropy.get(allTableNames.indexOf(tempArray.get(j)));
					mainTable = tempArray.get(j).toString();
				}
				else if(entropyValue < (float)entropy.get(allTableNames.indexOf(tempArray.get(j)))){
					entropyValue = (float)entropy.get(allTableNames.indexOf(tempArray.get(j)));
					mainTable = tempArray.get(j).toString();
				}
			}
			
			mainTables.add(mainTable);
			
		}
		
		ArrayList theClusterNumberEachTableShouldAdded = new ArrayList();//So this contains the number of the cluster that each table 
		//should be added contained in allTableThatGotProccessed
		for(int i=0;i<allTableThatGotProccessed.size();i++){
			String dataOfTableInProcess = HandlingQueries.getAllValuesOfTabel(allTableThatGotProccessed.get(i).toString());
			dataOfTableInProcess = dataOfTableInProcess.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\'", "").replaceAll("NULL", "");
			
			float qgramValue = 0;
			int clusterNoofTable = 0;
			
			for(int j=0;j<mainTables.size();j++){
				String dataOfMainInProcess = HandlingQueries.getAllValuesOfTabel(mainTables.get(j).toString());
				dataOfMainInProcess = dataOfMainInProcess.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\'", "").replaceAll("NULL", "");
				
				QGram qgram = new QGram(2); //This value should be decided by the user. He has to change the value and based on 
				//That have to identify the best configuration
				if(qgramValue == 0){
					qgramValue = (float) qgram.distance(dataOfTableInProcess, dataOfMainInProcess);
					clusterNoofTable = j;
				}else if(qgramValue > (float) qgram.distance(dataOfTableInProcess, dataOfMainInProcess)){
					qgramValue = (float) qgram.distance(dataOfTableInProcess, dataOfMainInProcess);
					clusterNoofTable = j;
				}
			}
			
			theClusterNumberEachTableShouldAdded.add(clusterNoofTable);
			
		}
		
		
		
		List<List<String>> newClustersOne = new ArrayList<List<String>>();
		//This is the plae where we add each table to the cluster that was chosen
		for(int i=0;i<newClusters.size();i++){
			if(theClusterNumberEachTableShouldAdded.contains(i)){
				ArrayList tempInfo = (ArrayList)newClusters.get(i);
				for(int j=0;j<theClusterNumberEachTableShouldAdded.size();j++){
					if((int)theClusterNumberEachTableShouldAdded.get(j) == i){
						tempInfo.add(allTableThatGotProccessed.get(j).toString());
					}
					
				}
				
				newClustersOne.add(tempInfo);
			}else{
				newClustersOne.add(newClusters.get(i));
			}
			
		}
		
		
		
		return newClustersOne;
		
		
	}

}
