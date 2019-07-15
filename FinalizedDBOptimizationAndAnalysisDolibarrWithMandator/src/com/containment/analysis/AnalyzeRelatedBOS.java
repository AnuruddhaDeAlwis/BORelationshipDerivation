package com.containment.analysis;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeRelatedBOS {
	
	//This is the class to analyse the business objects which have the relationships between each other and then return arraylist 
	//defining the business object relationships.
	
	public static List<List<String>> analyseBORelatinoshinps(ArrayList foriegnKeys, List<List<String>>finalClusters){
		
		List<List<String>> boRelationshipsAndCount = new ArrayList<List<String>>();
		
		ArrayList boRelatinoships = new ArrayList();//This will store the relationship between two BOs
		ArrayList noOfCalls = new ArrayList();//This will stre the total number of relationships between the identified BOs
		
		
		for(int i=0;i<foriegnKeys.size();i++){
			
			String splitted[] = foriegnKeys.get(i).toString().split("=");
			int one = -1;
			int two = -1;
			
			for(int j=0;j<finalClusters.size();j++){
				if(finalClusters.get(j).contains(splitted[0])){
					one = j;
				}else if(finalClusters.get(j).contains(splitted[1])){
					two = j;
				}
			}
			
			
			String stOne = one+"-"+two;
			String stTwo = two+"-"+one;
			
			if(!boRelatinoships.contains(stOne) && !boRelatinoships.contains(stTwo)){
				boRelatinoships.add(stOne);
				noOfCalls.add(1);
			}else if(boRelatinoships.contains(stOne)){
				int value = (int)noOfCalls.get(boRelatinoships.indexOf(stOne)) +1;
				noOfCalls.set(boRelatinoships.indexOf(stOne), value);
			}else if(boRelatinoships.contains(stTwo)){
				int value = (int)noOfCalls.get(boRelatinoships.indexOf(stTwo)) +1;
				noOfCalls.set(boRelatinoships.indexOf(stTwo), value);
			}
			
		}
		
		boRelationshipsAndCount.add(boRelatinoships);
		boRelationshipsAndCount.add(noOfCalls);
		
		return boRelationshipsAndCount;
	}

}
