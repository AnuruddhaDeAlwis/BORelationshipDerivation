package com.dynamic.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessLogFiles {
	
	/*
	 * This is the class that we use to process the log files in order to fileter out the SQL statesments that is needed to be processed.
	 * Specifically we follow the following steps.
	 * 1. We extract and catergories the logs based on the time that they got executed.
	 * 2. Then we have to idnetify the select, delete, update and delete operations and the tables related to them
	 * 3. These details will be used to evaluate the exclusive and inclusive contianment relationships.
	 * */
	
	
	/*
	 * This method is used to porces the execution log files related to the dolibarr system.
	 * fileLocation - the file location related to the file whic is going to get processed
	 * */
	public static List<List<String>>  processLogFiles(String fileLocation){
		List<List<String>> categorisedLogs = catergoriseLogs(readTheTextFile(fileLocation));
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("C:/DolibarrResulsts/logsPrint.txt", true));
			
			for(int i = 0; i< categorisedLogs.size(); i++){
				ArrayList temp = (ArrayList)categorisedLogs.get(i);
				
				for(int j = 0; j < temp.size(); j++){
					writer.write(temp.get(j).toString()+"\n\n");
					
				}
				
				writer.write("------------------------------------ \n\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		return categorisedLogs;
		
	}
	
	
	/*
	 * This is the method thwe we use to categorise the logs based on the time they got executed
	 * */
	public static List<List<String>> catergoriseLogs(String dataToProcess){
		
		String splitt[] = dataToProcess.split("@@2018-");
		System.out.println("Test");
		
		List<List<String>> categorisedLogs = new ArrayList<List<String>>();
		String time = "";
		ArrayList relatedTextArray = new ArrayList();
		//Now we split each string to verify to create a caterogirised 
		for(int i = 0; i < splitt.length; i++){
			
			String splitOne[] = splitt[i].split(" "); 
			if(splitOne.length > 5){
				if(time.equalsIgnoreCase("")){
					time = splitOne[0]+" "+splitOne[1];
					if(!changeTheText(time,splitt[i]).equalsIgnoreCase("nill")){
						relatedTextArray.add(changeTheText(time,splitt[i]));
					}
					
				}else{
					if(time.equalsIgnoreCase(splitOne[0]+" "+splitOne[1])){
						if(!changeTheText(time,splitt[i]).equalsIgnoreCase("nill")){
							relatedTextArray.add(changeTheText(time,splitt[i]));
						}
						
					}else{
						time = splitOne[0]+" "+splitOne[1];
						categorisedLogs.add(relatedTextArray);
						relatedTextArray = new ArrayList();
						if(!changeTheText(time,splitt[i]).equalsIgnoreCase("nill")){
							relatedTextArray.add(changeTheText(time,splitt[i]));
						}
						
					}
					
				}
			}
			
			
			
		}
		categorisedLogs.add(relatedTextArray);
		
		return categorisedLogs;
	}
	
	
	
	/*
	 * This method is to change the text based and only extract the text which contains CRUD operations
	 * */
	public static String changeTheText(String textToRemove, String totalString){
		String nextText = totalString.replace(textToRemove, "");
		if(nextText.contains("sql=")){
			nextText = nextText.replaceAll("sql=", "");
			return nextText;
		}
		return "nill";
	}
	
	

	/*
	 *This is used to read the log file form the given location
	 *fileLocation - the file location related to the file whic is going to get processed 
	 * */
	public static String readTheTextFile(String fileLocation){
		String logtoProcess = "";

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			FileReader fr = new FileReader(fileLocation);
			BufferedReader br = new BufferedReader(fr);

			
			String st;
			while ((st = br.readLine()) != null) {
				logtoProcess = logtoProcess + st;
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		
		return logtoProcess;
	}
	
}
