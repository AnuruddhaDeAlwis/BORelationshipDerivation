package com.containment.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class ForeignKeyAnalysis {
	
	//This is to analyze the code and extract the foriegn key relationship from it.
	static ArrayList allTheFKeys = new ArrayList();
	static ArrayList allTheOperations = new ArrayList();
	

	static String[] allTablesFrimSQL;
	static List<String> wordList;
	
	static String allTheTableInformation = "";
	static String allOperations = "";
	
		public static List<List<String>> identifyTablesFromQueries(String dataReadLocation, String DataWriteLocation, String OperationWriteLocation, String extraInfoLocation) throws Exception{
			String allInfromation = "";
			String allTables = "";
			
			
			
			
			try {
			    BufferedReader in = new BufferedReader(new FileReader(dataReadLocation));
			    String str;
			    while ((str = in.readLine()) != null)
			    	allInfromation = allInfromation + str+"@@@";
			    in.close();
			} catch (IOException e) {
			}
			
			
			
			
			String extraInfo = "";

			try {

				//br = new BufferedReader(new FileReader(FILENAME));
				FileReader fr = new FileReader(extraInfoLocation);
				BufferedReader br = new BufferedReader(fr);

				
				String st;
				while ((st = br.readLine()) != null) {
					extraInfo = extraInfo + st;
				}

			} catch (IOException e) {

				e.printStackTrace();

			}

			
			extraInfo = extraInfo.replace("\\s+", "");
			String [] extraData = extraInfo.split("@@@");	
			
			//The detials for the foriegn key tables which contians other information like eabr = email_addr_bean_rel
			ArrayList tableKey = new ArrayList();//Contains the names of the table keys
			ArrayList tableNames = new ArrayList();//Contians the table names
			
			for(int i=0; i< extraData.length; i++){
				String [] splittedData = extraData[i].split("=");
				tableKey.add(splittedData[1]);
				tableNames.add(splittedData[0]);
				
			}
			
			//The regex patterns
			String[] regexPatterns = {"(SELECT)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))","(UPDATE)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))",
					"(DELETE)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))","(INSERT)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))" };
			
			//The sqlQueries Extracted
			String[] sqlQueriesExtracted = allInfromation.split("@@@");
			
			
			//So here we get all the database table names. If that particular name is not there then there is no use of adding that 
			//particular value to the allTables.
			wordList = getDatabseTabelNames();
			
			for(int i=0;i<regexPatterns.length;i++){
				
				Pattern pone = Pattern.compile(regexPatterns[i], Pattern.DOTALL);
				
				
				for(int j=0;j<sqlQueriesExtracted.length;j++){
					Matcher mone = pone.matcher(sqlQueriesExtracted[j]);
					if (mone.find()) {
			    		String xxone = "";
			    	for(int a=0;a<mone.groupCount()-1;a++){
			    		//System.out.println("Value: "+m.group(a));
			    		xxone = mone.group(a);
			    		
			    		Pattern p = Pattern.compile("((\\w)+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z_ ]+\\.+(\\w+))",Pattern.DOTALL); //Best with all
						//Pattern p = Pattern.compile("[a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+)", Pattern.DOTALL);
						
						//System.out.println("Log :"+fileEntry);
							
					    	Matcher m = p.matcher(xxone);
					    	
					    	if (m.find()) {
					    		String xx = "";
					    	for(int b=0;b<m.groupCount()-1;b++){
					    		//System.out.println("Value: "+m.group(a));
					    		xx = m.group(b);
					    	}
					    	
					    	String[] betweenEqual = xx.split("=");
					    	String[] one = betweenEqual[0].split("\\.");
					    	String[] two = betweenEqual[1].split("\\.");
					    	
					    	String onezero = one[0].replaceAll("\\s+","");
					    	String twozero = two[0].replaceAll("\\s+","");
					    	
					    	if(wordList.contains(onezero) && wordList.contains(twozero)){
					    		allTables = allTables +xx+"@@@";
					    		allTheFKeys.add(xx);
					    		operationContainedInTheSQL(i);
					    	}else if(!wordList.contains(onezero) && !wordList.contains(twozero)){
								if(tableKey.contains(onezero) && tableKey.contains(twozero)){
									ArrayList<Integer> object1 = indexOfAll(onezero,tableKey);
									ArrayList<Integer> object2 = indexOfAll(twozero,tableKey);
									
									String obOne = "";
									String obTwo = "";
									for(int x = 0 ; x < object1.size(); x++){
										if(sqlQueriesExtracted[j].contains(tableNames.get(x).toString())){
											obOne = obOne + tableNames.get(x).toString();
													break;
										}
									}
									for(int y = 0 ; y < object2.size();y++){
										if(sqlQueriesExtracted[j].contains(tableNames.get(y).toString())){
											obTwo = obTwo + tableNames.get(y).toString();
													break;
										}
									}
									
									String datatoInsert = obOne+"."+one[1]+"="+obTwo+"."+two[1];
									allTheFKeys.add(datatoInsert);
									allTables = allTables +datatoInsert+"@@@";
									operationContainedInTheSQL(i);
								}
							}else if(!wordList.contains(onezero) && wordList.contains(twozero)){
								if(tableKey.contains(onezero)){
									ArrayList<Integer> object1 = indexOfAll(onezero,tableKey);
					
									
									String obOne = "";
							
									for(int x = 0 ; x < object1.size(); x++){
										if(sqlQueriesExtracted[j].contains(tableNames.get(x).toString())){
											obOne = obOne + tableNames.get(x).toString();
													break;
										}
									}
									
									
									
									String datatoInsert = obOne+"."+one[1]+"="+two[0]+"."+two[1];
									allTheFKeys.add(datatoInsert);
									allTables = allTables +datatoInsert+"@@@";
									operationContainedInTheSQL(i);
								}
							}else if(wordList.contains(onezero) && !wordList.contains(twozero)){
								if(tableKey.contains(twozero)){
									ArrayList<Integer> object2 = indexOfAll(twozero,tableKey);
									
									
									String obTwo = "";
									
									for(int y = 0 ; y < object2.size();y++){
										if(sqlQueriesExtracted[j].contains(tableNames.get(y).toString())){
											obTwo = obTwo + tableNames.get(y).toString();
													break;
										}
									}
									
									
									String datatoInsert = one[0]+"."+one[1]+"="+obTwo+"."+two[1];
									allTheFKeys.add(datatoInsert);
									allTables = allTables +datatoInsert+"@@@";
									operationContainedInTheSQL(i);
								}
							}
					    	
					    	
					    	//System.out.println(xx);
					    	
					    	}
			    	}
			    	
			    	
			    	
			    	
					
				}
				}
				
			}
			
		    	
		    	
		    	
		 	    try {
		 	    	BufferedWriter writer = new BufferedWriter(new FileWriter(DataWriteLocation));
					writer.write(allTables);
				    writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 	    
		 	    
		 	   try {
		 	    	BufferedWriter writer = new BufferedWriter(new FileWriter(OperationWriteLocation));
					writer.write(allOperations);
				    writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 	   
		 	   
		 	   
		 	  List<List<String>> fkeysAndOperations = new ArrayList<List<String>>();
		 	  fkeysAndOperations.add(allTheFKeys);
		 	  fkeysAndOperations.add(allTheOperations);
		 	  
		 	  return fkeysAndOperations;
			
		}
		
		
		
		public static void operationContainedInTheSQL(int type){
			if(type == 0){
				allOperations = allOperations+"SELECT"+"@@@";
				allTheOperations.add("SELECT");
			}else if(type == 1){
				allOperations = allOperations+"UPDATE"+"@@@";
				allTheOperations.add("UPDATE");
			}else if(type == 2){
				allOperations = allOperations+"DELETE"+"@@@";
				allTheOperations.add("DELETE");
			}else if(type == 3){
				allOperations = allOperations+"INSERT"+"@@@";
				allTheOperations.add("INSERT");
			}
			
		}
		
		
	
		
		
		public static void listFilesForFolder(final File folder) throws Exception {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {

					String sqlDataSummarized = FileUtils.readFileToString(new File(fileEntry.getAbsolutePath()), "UTF-8");
					allTheTableInformation = allTheTableInformation +sqlDataSummarized+"@@@";

				}

			}


		}
		
		
		
		public static ArrayList getDatabseTabelNames(){
			ArrayList allTableNames = new ArrayList();
			try{  
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/dolibarr","root","");  
				//here sonoo is database name, root is username and password  
				Statement stmt=con.createStatement();  
				ResultSet rs=stmt.executeQuery("SHOW TABLES FROM dolibarr");  
				while(rs.next())  
					allTableNames.add(rs.getObject(1)); 
					con.close();  
			}catch(Exception e){ 
				System.out.println(e);
				}  
			
			return allTableNames;
		}

	
		
		
		static ArrayList<Integer> indexOfAll(Object obj, ArrayList list){
		    ArrayList<Integer> indexList = new ArrayList<Integer>();
		    for (int i = 0; i < list.size(); i++)
		        if(obj.equals(list.get(i)))
		            indexList.add(i);
		    return indexList;
		}
		
		
		
	
}
