package com.containment.newanalysis;

import java.util.ArrayList;
import java.util.List;

public class AggregationAnalysis {
	/*
	 * This class is created to analyse the aggregation relationships between the business objects.
	 * Basically we define two types of aggregations, the optional aggregation and mandatory aggegation
	 * If the relationship is 1 - 1..* then it is a mandotory one 
	 * If it is 1 - 0..* then it is a optional one
	 * Furthermore here we consider the effect of the SELECT statements. If the related BOs only have SELECT
	 * statment based on the foriegn key relationship then those BOs should be contained together becasue all the time the
	 * SELECT happens through that particular relationship. However if there are independent select operations related to the
	 * tables then they can be seperated, since there can be accessed independently with out using the foriegn key relationship
	 * */
	
	
	/*
	 * This method is repsonsible for analysing the aggregation relationship
	 * allBoClusters - the table clusters based on same BO relationships Only with insert and delete
	 * allOPClusters - the operations related to the clusters
	 * allBoClustersWithAll - the table clusters based on same BO relationships
	 * allOPClustersWithAll -  the operations related to the clusters
	 * */
	public static void aggregationAnalysis(List<List<String>> allBoClusters, List<List<String>> allOPClusters, 
			List<List<String>> allBoClustersWithAll, List<List<String>> allOPClustersWithAll,  
			List<List<String>> allNormalKeytablesDetials, int boProcessing){

		//Now this is actually aggegation relationship. Here we evaluate two types. Mandotory aggregation and optional aggegation.
			ArrayList operationsRelated = new ArrayList();
			ArrayList bosRelated = new ArrayList();
			
			operationsRelated =  (ArrayList)allOPClusters.get(boProcessing);
			bosRelated =  (ArrayList)allBoClusters.get(boProcessing);	
			
			
			//there can be multiples insert operations. As such we have to evaluate all of them.
			int count = 0;
			ArrayList indexes = new ArrayList(); //indexes of the relationships which have insert operations
			for(int j = 0; j < operationsRelated.size(); j++){
				if(operationsRelated.get(j).toString().equalsIgnoreCase("INSERT")){
					count++;
					indexes.add(j);
				}
			}
			
			if(count == 1){
				//This is a situation where there is only one Insert operation in the given group
				int indexofInsert = operationsRelated.indexOf("INSERT");
				String oldString = bosRelated.get(indexofInsert).toString();
				String splitt[] = oldString.split("=");
				String newString = splitt[1]+"="+splitt[0];
				
//				operationsRelated.remove(indexofInsert);
//				bosRelated.remove(indexofInsert);
				
				for(int k = 0; k < operationsRelated.size(); k++){
					//Here we try to evaluate the effect that comes from the foreignkey SELECT operation
					if(bosRelated.get(k).toString().equalsIgnoreCase(newString) || bosRelated.get(k).toString().equalsIgnoreCase(oldString) && operationsRelated.get(k).toString().equalsIgnoreCase("SELECT")){
						//Check whether ther are independent select operations related to the particular BO
						
						boolean independency = VerifyIndependentSelectOps.selectOperationAnalysis(splitt[1], splitt[0], allNormalKeytablesDetials);
						
						
						
						if(independency){
							//if independency is true it means that there are independent select operations related to the BOs.
							//As such there is no problem of sperating them into different microservices
							//now we have to check the cardinalities of the tables.
							//Here we check the cardinality of the tables related by insert operation only because there is no point
							//of thinking about the select and delete operations realted to differnt tables in the BOS
							CardinalityAnalysis.cardinalityEvalution(splitt);
							System.out.println("The tables: "+oldString+" are aggregated and can be seperated, because there are independent select"
									+ "operations related to the tabels \n");
							
						}else{
							//If it is false then it mean then this means that it is not good to seperate the BOs into
							//different microservices because the are selected together always
							//Now the cardinality check shold be done.
							//Here we check the cardinality of the tables related by insert operation only because there is no point
							//of thinking about the select and delete operations realted to differnt tables in the BOS
							CardinalityAnalysis.cardinalityEvalution(splitt);
							System.out.println("The tables: "+oldString+" are aggregated and cannot be seperated, because there are no independent select"
									+ "operations related to the tabels as such every select operation should be perfomred together \n");
						}
					}else{
						//Now this means that there are no select operations related as such it is okay to seperate the bos. 
						//because there are no select operations perform using the foriegn key relationship.
						CardinalityAnalysis.cardinalityEvalution(splitt);
						System.out.println("The tables: "+oldString+" are aggregated and can be seperated, because there "
								+ "no select operations with foriegn keys to the tabels \n");
						
					}
				}
				
				
				
				
			}else if(count > 1){
				//This means that there are multiple insert operations in the given group. As such all of them should be evaluated.
				
				for(int l =0;  l < indexes.size(); l++){
					
					int indexofInsert = (int)indexes.get(l);
					String oldString = bosRelated.get(indexofInsert).toString();
					String splitt[] = oldString.split("=");
					String newString = splitt[1]+"="+splitt[0];
					
//					operationsRelated.remove(indexofInsert);
//					bosRelated.remove(indexofInsert);
					
					for(int k = 0; k < operationsRelated.size(); k++){
						//Here we try to evaluate the effect that comes from the foreignkey SELECT operation
						if(bosRelated.get(k).toString().equalsIgnoreCase(newString) || bosRelated.get(k).toString().equalsIgnoreCase(oldString) && operationsRelated.get(k).toString().equalsIgnoreCase("SELECT")){
							//Check whether ther are independent select operations related to the particular BO
							
							boolean independency = VerifyIndependentSelectOps.selectOperationAnalysis(splitt[1], splitt[0], allNormalKeytablesDetials);
							
							
							
							if(independency){
								//if independency is true it means that there are independent select operations related to the BOs.
								//As such there is no problem of sperating them in to different microservices
								//now we have to check the cardinalities of the tables.
								//Here we check the cardinality of the tables related by insert operation only because there is no point
								//of thinking about the select and delete operations realted to differnt tables in the BOS
								CardinalityAnalysis.cardinalityEvalution(splitt);
								System.out.println("The tables: "+oldString+" are assocaiated and can be seperated, because there are independent select"
										+ "operations related to the tabels \n");
								
							}else{
								//If it is false then it mean then this means that it is not good to seperate the BOs into
								//different microservices because the are selected together always
								//Now the cardinality check shold be done.
								//Here we check the cardinality of the tables related by insert operation only because there is no point
								//of thinking about the select and delete operations realted to differnt tables in the BOS
								CardinalityAnalysis.cardinalityEvalution(splitt);
								System.out.println("The tables: "+oldString+" are assocaiated and cannot be seperated, because there are independent select"
										+ "operations related to the tabels \n");
							}
						}else{
							//Now this means that there are no select operations related as such no it is okay to seperate the bos.
							CardinalityAnalysis.cardinalityEvalution(splitt);
							System.out.println("The tables: "+oldString+" are assocaiated and can be seperated, because there "
									+ "no select operations with foriegn keys to the tabels \n");
							
						}
					}
					
					
					
					
				}
				
				
				
			}else{
				//No need of doing anything. This basically means that there are no aggregation operations between the BOs.
			}
			
			
			
			
			
			
			
		
	}

}
