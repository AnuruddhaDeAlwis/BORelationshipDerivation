package com.containment.newanalysis;
import java.util.ArrayList;
import java.util.List;

public class ExclusiveOrInclusiveVerification {
	
	//This is the class which is used to analyse the containment types.
	//First we evelauate whether there are insert and delete operations related to the same business object. 
	//Then we check whether there are multiple insert and delete relationships with other BOs. If so it would be an inclusive containment
	//Else it would be an exclusive containment. After identifying whether it is inclusive or exclusive we have to identify whether they
	//are optional or mandotory. Another thing is to evalute whether there are independent select operations related to the BOS. If so
	//we can seperate BOs into different microservices however, if it is not the case then better to keep them in th same container
	//because it will create high communication overhead.
	
	
	/*
	 * This method is repsonsible for analysing the association relationship
	 * allBoClusters - the table clusters based on same BO relationships Only with insert and delete
	 * allOPClusters - the operations related to the clusters
	 * allBoClustersWithAll - the table clusters based on same BO relationships
	 * allOPClustersWithAll -  the operations related to the clusters
	 * allNormalKeytablesDetials - the normal tables with their related operations
	 * boProcessing - the number of the bo that we are processing
	 * */
	public static void exclusiveAndInclusiveAnalysis(List<List<String>> allBoClusters, List<List<String>> allOPClusters, 
			List<List<String>> allBoClustersWithAll, List<List<String>> allOPClustersWithAll,  
			List<List<String>> allNormalKeytablesDetials, int boProcessing){
		
		//Now this is actually containment relationship. Here we evaluate two types. Mandotory association and optional association.
		ArrayList operationsRelated = new ArrayList();
		ArrayList bosRelated = new ArrayList();
		
		operationsRelated =  (ArrayList)allOPClusters.get(boProcessing);
		bosRelated =  (ArrayList)allBoClusters.get(boProcessing);	
		
		//1. we have to evalate whether the insert and delete are reated to the same BO. If not then it is an aggregation.
		ArrayList tempBORelated = bosRelated; //This is to create a temporary arraylist to evalute the BO which are similar
		ArrayList tempOpRelates = operationsRelated; //This is to create temporary arraylist to evaluate the operations related to same BOs
		
		ArrayList tempBORelated1 = bosRelated; //This is to create a temporary arraylist to evalute the BO which are similar
		ArrayList tempOpRelates1 = operationsRelated; //This is to create temporary arraylist to evaluate the operations related to same BOs
		
		
		List<List<String>> allBosGroups = new ArrayList<List<String>>(); //The groups which have similare tables in the relatioship
		List<List<String>> allOPGroups = new ArrayList<List<String>>(); //The groups which have operations in the relatioship
		
		while(tempBORelated.size() > 1){
			ArrayList boWhichAreRelated = new ArrayList(); //To add the Bos which are related
			ArrayList opsWhichAreRelated = new ArrayList(); //To add the operations which are related to the above (boWhichAreRelated) BOs
			
			String boToProcess = tempBORelated.get(0).toString();
			String splitted[] = boToProcess.split("=");
			String theBoTurned = splitted[1]+"="+splitted[0];
			for(int i = 1; i < tempBORelated.size(); i++ ){
				if(tempBORelated.get(i).toString().equalsIgnoreCase(boToProcess) || tempBORelated.get(i).toString().equalsIgnoreCase(theBoTurned)){
					boWhichAreRelated.add(tempBORelated.get(i));
					opsWhichAreRelated.add(tempOpRelates.get(i));
					tempBORelated1.remove(i);
					tempOpRelates1.remove(i);
				}
			}
			
			allBosGroups.add(boWhichAreRelated);
			allOPGroups.add(opsWhichAreRelated);
			
			tempBORelated = tempBORelated1;
			tempOpRelates = tempOpRelates1;
		}
		
		
		int isExclusive = 0; //if it remians 0 then it is aggreagation else it is exclusive contianment
		ArrayList insertAndDeleteBos = new ArrayList(); //This contains operations related by insert and delete operations
		for(int i = 0; i < allOPGroups.size(); i++){
			ArrayList temp = (ArrayList)allOPGroups.get(i);
			
			if((temp.contains("INSERT") || temp.contains("Insert") || temp.contains("insert")) &&
					(temp.contains("DELETE") || temp.contains("Delete") || temp.contains("delete"))){
				isExclusive++;
				insertAndDeleteBos.add(i);
			}
			
		}
		
		if(isExclusive == 0){
			//This means the bos have an aggregation relationship
			//So we can directly call the Association analysis class finctionality here
			AggregationAnalysis.aggregationAnalysis(allBoClusters, allOPClusters, allBoClustersWithAll, allOPClustersWithAll, allNormalKeytablesDetials, boProcessing);			
		}else{
			//Then it is an exclusive containment reationship
			//2. if we have insert and delete operations related to the same BO then we have to check whether there are multiple
			//insert and deletes related to the same bo. [However we do not perform it here. Because if a BO has insert and delete operations
			//related to it then it should be an exclusive containment relationship]
			
			for(int i = 0; i < insertAndDeleteBos.size(); i++){
				ArrayList opsOfTheBOs = (ArrayList)allOPGroups.get(i);
				ArrayList bosOftheOPs = (ArrayList)allBosGroups.get(i);
				String splitt[] = bosOftheOPs.get(0).toString().split("=");
				
				if(opsOfTheBOs.contains("SELECT") || opsOfTheBOs.contains("Select") || opsOfTheBOs.contains("select")){
					
					boolean independency = VerifyIndependentSelectOps.selectOperationAnalysis(splitt[1], splitt[0], allNormalKeytablesDetials);
					
					if(independency){
						//if independency is true it means that there are independent select operations related to the BOs.
						//As such there is no problem of sperating them in to different microservices
						//now we have to check the cardinalities of the tables.
						//Here we check the cardinality of the tables related by insert operation only because there is no point
						//of thinking about the select and delete operations realted to differnt tables in the BOS
						CardinalityAnalysis.cardinalityEvalution(splitt);
						System.out.println("The tables: "+splitt[0]+"="+splitt[1]+" are exclusively contined and can be included in seperate"
								+ "microservices resides in the same container, because there are no independent select operations to them \n");
						
					}else{
						//If it is false then it mean then this means that it is not good to seperate the BOs into
						//different microservices because the are selected together always
						//Now the cardinality check shold be done.
						//Here we check the cardinality of the tables related by insert operation only because there is no point
						//of thinking about the select and delete operations realted to differnt tables in the BOS
						CardinalityAnalysis.cardinalityEvalution(splitt);
						System.out.println("The tables: "+splitt[0]+"="+splitt[1]+" are exclusively and cannot be seperated, because there are no independent select"
								+ "operations related to the tabels \n");
					}
					
				}else{
					
					//Now this means that there are no select operations related as such no it is okay to seperate the bos.
						CardinalityAnalysis.cardinalityEvalution(splitt);
						System.out.println("The tables: "+splitt[0]+"="+splitt[1]+" are exclusively contained and can be included in seperate"
								+ "microservices resides in the same container, because there are no related select operations to them \n");
					
					
				}
				
				
				
				
			}
			
			
		}
		
		
	
	}
	

}
