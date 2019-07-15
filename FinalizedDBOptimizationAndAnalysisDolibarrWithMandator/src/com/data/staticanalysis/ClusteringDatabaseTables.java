package com.data.staticanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusteringDatabaseTables {
	static ArrayList nameOfTheTables = new ArrayList();
	static List<List<Integer>> outer = new ArrayList<List<Integer>>();//contains the number of relationships each table have with the others
	static ArrayList<Integer> uniqueNumbers = new ArrayList<Integer>();
	static ArrayList relatinoshipColumns = new ArrayList();
	
	
	static List<List<String>> clusterignGroups = new ArrayList<List<String>>(); //conatins the tables that got clustered.
	
	//To return the generated clustring group table names
	public static List<List<String>> generateTheClusteringGroups(String fileToRead){
		generateTheStaticCountExcel(fileToRead);
		return clusterignGroups;
	}
	
	//To return the number of relationship each of the table having with the other.
	public static List<List<Integer>> generateTheClusteringNumberOfRelationships(String fileToRead){
		generateTheStaticCountExcel(fileToRead);
		return outer;
	}
	
	//To return the table Names based on the order they got processed
	public static List<List<String>> tableNamesGotProcessed(String fileToRead){
		List<List<String>> tableNamesAndColumns = new ArrayList<List<String>>();
		generateTheStaticCountExcel(fileToRead);
		tableNamesAndColumns.add(nameOfTheTables);
		tableNamesAndColumns.add(relatinoshipColumns);
		return tableNamesAndColumns;
	}
	
	//Just Table names without duplications
	public static ArrayList tableNamesGotProcessedWithoutDup(String fileToRead){
		generateTheStaticCountExcel(fileToRead);
		ArrayList al = new ArrayList();
		// add elements to al, including duplicates
		Set<String> hs = new HashSet<>();
		hs.addAll(nameOfTheTables);
		al.addAll(hs);
		return al;
	}
	
	
	public static void generateTheStaticCountExcel(String fileToRead){
		String allInfromation = "";
		
		try {
		    BufferedReader in = new BufferedReader(new FileReader(fileToRead));
		    String str;
		    while ((str = in.readLine()) != null)
		    	allInfromation = allInfromation + str;
		    in.close();
		} catch (IOException e) {
		}
		
		allInfromation = allInfromation.replace("\n", "").replace("\r", "");
		
		String [] foreignKeys = allInfromation.split("@@@");
		
		for(int i=0; i<foreignKeys.length; i++){
			String [] twoAsideEqual = foreignKeys[i].split("=");
			String [] sideOne = twoAsideEqual[0].split("\\.");
			String [] sideTwo = twoAsideEqual[1].split("\\.");
			sideOne[0] = sideOne[0].replaceAll("\\s+","");
			sideTwo[0] = sideTwo[0].replaceAll("\\s+","");
			sideOne[1] = sideOne[1].replaceAll("\\s+","");
			sideTwo[1] = sideTwo[1].replaceAll("\\s+","");
			
			if(i == 0){
				nameOfTheTables.add(sideOne[0]);
				nameOfTheTables.add(sideTwo[0]);
				
				relatinoshipColumns.add(sideOne[1]);
				relatinoshipColumns.add(sideTwo[1]);
				
				
				List<Integer> one = new ArrayList<Integer>();
				one = arrayListValueAdding(nameOfTheTables.indexOf(sideTwo[0]),one);
				one.add(nameOfTheTables.indexOf(sideTwo[0]), 1);
				List<Integer> two = new ArrayList<Integer>();
				two = arrayListValueAdding(nameOfTheTables.indexOf(sideOne[0]),two);
				two.add(nameOfTheTables.indexOf(sideOne[0]), 1);
				
				outer.add(one);
				outer.add(two);
				
			}else if(!nameOfTheTables.contains(sideOne[0]) && !nameOfTheTables.contains(sideTwo[0])){
				
				nameOfTheTables.add(sideOne[0]);
				nameOfTheTables.add(sideTwo[0]);
				relatinoshipColumns.add(sideOne[1]);
				relatinoshipColumns.add(sideTwo[1]);
				
				List<Integer> one = new ArrayList<Integer>();
				one = arrayListValueAdding(nameOfTheTables.indexOf(sideTwo[0]),one);
				one.set(nameOfTheTables.indexOf(sideTwo[0]), 1);
				List<Integer> two = new ArrayList<Integer>();
				two = arrayListValueAdding(nameOfTheTables.indexOf(sideOne[0]),two);
				two.set(nameOfTheTables.indexOf(sideOne[0]), 1);
				
				outer.add(one);
				outer.add(two);
			}else if(nameOfTheTables.contains(sideOne[0]) && nameOfTheTables.contains(sideTwo[0])){
				
				int indexOne = nameOfTheTables.indexOf(sideOne[0]);
				int indexTwo = nameOfTheTables.indexOf(sideTwo[0]);
				
				List<Integer> one = outer.get(indexOne);
				List<Integer> two = outer.get(indexTwo);
				
			
				one = arrayListValueAdding(indexTwo,one);
				one.set(indexTwo, one.get(indexTwo)+1);
				two = arrayListValueAdding(indexOne,two);
				two.set(indexOne, two.get(indexOne)+1);
				
				outer.set(indexOne, one);
				outer.set(indexTwo, two);
				
			}else if(!nameOfTheTables.contains(sideOne[0]) && nameOfTheTables.contains(sideTwo[0])){
				
				nameOfTheTables.add(sideOne[0]);
				relatinoshipColumns.add(sideOne[1]);
				
				int indexOne = nameOfTheTables.indexOf(sideOne[0]);
				int indexTwo = nameOfTheTables.indexOf(sideTwo[0]);
				
				List<Integer> one = new ArrayList<Integer>();
				one = arrayListValueAdding(indexTwo,one);
				one.set(nameOfTheTables.indexOf(sideTwo[0]), 1);
				outer.add(one);
				
				
				List<Integer> two = outer.get(indexTwo);
				two = arrayListValueAdding(indexOne,two);
				two.set(indexOne, two.get(indexOne)+1);
				outer.set(indexTwo, two);
			}else if(nameOfTheTables.contains(sideOne[0]) && !nameOfTheTables.contains(sideTwo[0])){
				
				nameOfTheTables.add(sideTwo[0]);
				relatinoshipColumns.add(sideTwo[1]);
				int indexOne = nameOfTheTables.indexOf(sideOne[0]);
				int indexTwo = nameOfTheTables.indexOf(sideTwo[0]);
				
				List<Integer> two = new ArrayList<Integer>();
				two = arrayListValueAdding(indexOne,two);
				two.set(nameOfTheTables.indexOf(sideOne[0]), 1);
				outer.add(two);
				
				
				List<Integer> one = outer.get(indexTwo);
				one = arrayListValueAdding(indexTwo,one);
				one.set(indexOne, one.get(indexOne)+1);
				outer.set(indexTwo, one);
			}
			
			
			}
	
		
		
		for(int i=0;i<outer.size();i++){
			int highest = 0; //get the highest relationship
			int total = 0;
			int totalNonZero =0;
			List<Integer> one = outer.get(i);
			
			for(int j=0;j<one.size();j++){
				//System.out.print(one.get(j)+",");
				if((int)one.get(j)>highest){
					highest = (int)one.get(j);
				}
				
				total = total + (int)one.get(j);
				
				if((int)one.get(j) > 0){
					totalNonZero++;
				}
				if(!uniqueNumbers.contains((int)one.get(j))){
					uniqueNumbers.add((int)one.get(j));
				}
			}
			
			Collections.sort(uniqueNumbers);
			float medianValue = (float)total/totalNonZero;
			//medianValue = (medianValue + highest)/2;
			printingTheTableNamesAboveMedian(0,i);
			uniqueNumbers.clear();
		}
		
		checkSubGroups();
		
//		for(int i=0;i<outer.size();i++){
//			printingTheTableNamesAboveMedian(0,i);
//		}
	}
	
	
	
	
	//check subgroups in clusters
	public static void checkSubGroups(){
		
		ArrayList positionsToRemove = new ArrayList();
		
		for(int i=0;i<clusterignGroups.size();i++){
			ArrayList tempOne = (ArrayList) clusterignGroups.get(i);
			
			for(int j=0;j<clusterignGroups.size();j++){
				int count = 0;
				if(j!=i){
					ArrayList tempTwo = (ArrayList) clusterignGroups.get(j);
					
					for(int x=0;x<tempTwo.size();x++){
						if(tempOne.contains(tempTwo.get(x))){
							count++;
						}
					}
					
					if((float)count/tempTwo.size() > 0.65 && !positionsToRemove.contains(j) && tempOne.size() > tempTwo.size()){
						positionsToRemove.add(j);
						break;
					}
					
				}
				
			}
			
		}
		
		newClusterCreatedAfterRemoving(positionsToRemove);
	}
	
	
	public static void newClusterCreatedAfterRemoving(ArrayList positionsToRemove){
		List<List<String>> reclusterignGroups = new ArrayList<List<String>>();
		
		for(int i=0;i<clusterignGroups.size();i++){
			if(!positionsToRemove.contains(i)){
				reclusterignGroups.add(clusterignGroups.get(i));
			}
		}
		
		clusterignGroups = reclusterignGroups;
	}
	
	
	
	
	
	
	public static ArrayList returnMedianValues(String fileToRead){
		generateTheStaticCountExcel(fileToRead);
		ArrayList medianValues = new ArrayList();
		for(int i=0;i<outer.size();i++){
			int total = 0;
			int totalNonZero =0;
			List<Integer> one = outer.get(i);
			
			for(int j=0;j<one.size();j++){
				//System.out.print(one.get(j)+",");
				total = total + (int)one.get(j);
				
				if((int)one.get(j) > 0){
					totalNonZero++;
				}
				if(!uniqueNumbers.contains((int)one.get(j))){
					uniqueNumbers.add((int)one.get(j));
				}
			}
			
			Collections.sort(uniqueNumbers);
			float medianValue = (float)total/totalNonZero;
			medianValues.add(medianValue);
			uniqueNumbers.clear();
		}
		
		return medianValues;
		
		
	}
	
	
	
	public static ArrayList arrayListValueAdding(int position, List<Integer> one){
		
		for(int i=0; i<=position;i++){
			try {
				one.get( i );
			} catch ( IndexOutOfBoundsException e ) {
				one.add( i, 0 );
			}
		}
		
		return (ArrayList) one;
	}
	
	
	
	public static void printingTheTableNamesAboveMedian(float median, int arrayListNo){
		
		if(clusterignGroups.size() == 0){
			ArrayList cluster = new ArrayList();
			List<Integer> one = outer.get(arrayListNo);
			if(one.size()>0){
				cluster.add(nameOfTheTables.get(arrayListNo));
				
				for(int j=0;j<one.size();j++){
					if(one.get(j) > median && !cluster.contains(nameOfTheTables.get(j))){
						cluster.add(nameOfTheTables.get(j));
					}
				}
				clusterignGroups.add(cluster);
			}
			
			
		}else{
			List<Integer> one = outer.get(arrayListNo);
			if(one.size()>0){
			int contained = 0;
			
			for(int i = 0;i<clusterignGroups.size();i++){
				ArrayList clustertemp = (ArrayList) clusterignGroups.get(i);
				
				if(clustertemp.contains(nameOfTheTables.get(arrayListNo)) && clustertemp.indexOf(nameOfTheTables.get(arrayListNo)) == 0){
					contained =1;
					for(int j=0;j<one.size();j++){
						if(one.get(j) > median && !clustertemp.contains(nameOfTheTables.get(j))){
							clustertemp.add(nameOfTheTables.get(j));
						}
					}
					
				}
				
				clusterignGroups.set(i, clustertemp);
			}
			
			if(contained == 0){
				ArrayList cluster = new ArrayList();
				cluster.add(nameOfTheTables.get(arrayListNo));
				
				for(int j=0;j<one.size();j++){
					if(one.get(j) > median && !cluster.contains(nameOfTheTables.get(j))){
						cluster.add(nameOfTheTables.get(j));
					}
				}
				clusterignGroups.add(cluster);
			}
			}
			
		}
		
		

		
		
	}
	
	
	
}
