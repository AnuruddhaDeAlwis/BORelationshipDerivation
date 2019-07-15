package com.containment.newanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.internal.core.util.HandleFactory;

import com.data.queries.HandlingQueries;
 
public class CardinalityAnalysis {
	
	/*
	 * This is the class which analyses the cardinality between different BOS and return the related details to the 
	 * ContainmentVerification class
	 * */
	
	/*
	 * This is the method which evaluates the cardinality of the business objects
	 * tab1 - the table one related of the relationship
	 * tab2 - table two of the relationship
	 * */
	public static String cardinalityEvalution(String tabs[]){
		String value = tableKeyIdentification(tabs);
		System.out.println("Cardinality is: "+value);
		return value;
		
	}
	
	
	
	
	/*
	 * This is the method that we use to identify the tables primary keys and foriegn keys. The we check the cardinality of the tables
	 * based on that.
	 * */
	public static String tableKeyIdentification(String tabs[]){
		
	
		
		List<List<String>> tableOneKeys = new ArrayList<List<String>>();
		List<List<String>> tableTwokeys = new ArrayList<List<String>>();
		
		
			 tableOneKeys = HandlingQueries.getTheTableSchema(tabs[0]); //get the databse table schema
			 tableTwokeys = HandlingQueries.getTheTableSchema(tabs[1]);
			
			
		
		
	
		System.out.println("test");
		//Now we have to evaluate which two columns are related.
		//For this first we check which key is the subset of the other
		//Then we check the one of the columns in the other tables contains subset of values from a given one. 
		//If it is a subset then it is 0..* connection however we have to check whether ther are multiples for the same id to check *. 
		//In this situation we simply ignore it because we just want to know whether it is 0 or 1 only.
		// If it is 0 then it is optional association. Else it is mandotory.
		
		ArrayList tableOnePkeys = (ArrayList)tableOneKeys.get(0);
		ArrayList tableTwoPkeys = (ArrayList)tableTwokeys.get(0);
		
		ArrayList tableOneFKeys =  (ArrayList)tableOneKeys.get(1);
		ArrayList tableTwoFkeys =  (ArrayList)tableTwokeys.get(1);
				
		
		int found = 0;
		
		//In this case the table one is the main one
		for(int i = 0; i < tableOnePkeys.size(); i++){
			
			ArrayList idsFound = HandlingQueries.getAllIdValuesFromTable(tableOnePkeys.get(i).toString(), tabs[0]);
			
			for(int j = 0; j < tableTwoFkeys.size(); j++){
				ArrayList fIdsFound = HandlingQueries.getAllIdValuesFromTable(tableTwoFkeys.get(j).toString(), tabs[1]);
				
				if(fIdsFound.size() < idsFound.size() && idsFound.containsAll(fIdsFound)){
					found = 1;
				}else if(fIdsFound.size() >= idsFound.size() && fIdsFound.containsAll(idsFound)){
					found = 2;
				}
				
			}
			
			
		}
		
		
		if(found == 1){
			return "optional association";
		}else if(found == 2){
			return "mandatory association";
		}else{
			//now since the found is 0 it means that the table one is not the main one. As such we have to check table two
			for(int i = 0; i < tableTwoPkeys.size(); i++){
				
				ArrayList idsFound = HandlingQueries.getAllIdValuesFromTable(tableTwoPkeys.get(i).toString(), tabs[1]);
				
				for(int j = 0; j < tableOneFKeys.size(); j++){
					ArrayList fIdsFound = HandlingQueries.getAllIdValuesFromTable(tableOneFKeys.get(j).toString(), tabs[0]);
					
					if(fIdsFound.size() < idsFound.size() && idsFound.containsAll(fIdsFound)){
						found = 1;
					}else if(fIdsFound.size() >= idsFound.size() && fIdsFound.containsAll(idsFound)){
						found = 2;
					}
					
				}
				
				
			}
			
			
			
			if(found == 1){
				return "optional association";
			}else if(found == 2){
				return "mandatory association";
			}else{
				
				return "cardinality not found";
			}
		}
		
		
		
		
		
	}

}
