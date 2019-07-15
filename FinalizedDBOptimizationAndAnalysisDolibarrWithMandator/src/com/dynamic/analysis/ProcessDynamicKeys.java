package com.dynamic.analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This is the class the we used to process the dynamic CRUD operations. Basically we do the follwing steps
 * 1. Get all the operations that we have idnetified from the class ProcessLogFiles
 * 2. Then we analyse the whether there are foriegnk key relationships. If so we identify them with the following information
 * 	The tables related, Operations done
 * 3. If it does not have a foriegn key relationship then we identify the 
 * 	operation and the table which got executed
 * */

public class ProcessDynamicKeys {
	
	static ArrayList wordList = new ArrayList();
	
	public static List<List<String>>  processDynamicKeyInformation(List<List<String>> categorisedLogs, List<List<String>> givenNameOfTables){
		
		List<List<String>> operationsAndTables = new ArrayList<List<String>>();
		
		//Processing all the sql queries identified
		for(int i=0; i < categorisedLogs.size(); i++ ){
			
			ArrayList tempOne = (ArrayList)categorisedLogs.get(i);
			ArrayList tempArraylist = new ArrayList(); //To keep the process data related to the operations
			
		    wordList = getDatabseTabelNames();
			
			//processing each timely categorised set of sql operations
			for(int j = 0; j < tempOne.size(); j++ ){
				
				String foriegnKey = analyseForeignKeys(tempOne.get(j).toString(), givenNameOfTables);
				if(!foriegnKey.equalsIgnoreCase("nill")){
					tempArraylist.add(foriegnKey);
				}else{
					String normalKey = analyseNormalKeys(tempOne.get(j).toString(), givenNameOfTables);
					if(!normalKey.equalsIgnoreCase("nill") || !normalKey.isEmpty()){
						tempArraylist.add(normalKey);
					}
					
				}
				
				
				
				
			}
			operationsAndTables.add(tempArraylist);
			
		}
		
		return operationsAndTables;
	}
	
	
	//To Analyse the Foreign key relationships. This will return the realted table and the operations if identified.
	//Else it will return nill
	public static String analyseForeignKeys(String query, List<List<String>> givenNameOfTables){
		//The regex patterns
		String textToReturn =  "";
		
		//The detials for the foriegn key tables which contians other information like eabr = email_addr_bean_rel
		ArrayList tableKey = (ArrayList)givenNameOfTables.get(0);//Contains the names of the table keys
		ArrayList tableNames = (ArrayList)givenNameOfTables.get(1);//Contians the table names
		
		
		ArrayList operationType = new ArrayList();
		operationType.add("SELECT");
		operationType.add("UPDATE");
		operationType.add("DELETE");
		operationType.add("INSERT");
		
		String[] regexPatterns = {"(SELECT)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))","(UPDATE)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))",
				"(DELETE)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))","(INSERT)+(.)+([a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+))" };
		
		for(int i=0;i<regexPatterns.length;i++){
			
			
			

			
			Pattern pone = Pattern.compile(regexPatterns[i], Pattern.DOTALL);
			
			
			
				Matcher mone = pone.matcher(query);
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
				    	String tab = "";
				    	
				    	if(wordList.contains(onezero) && wordList.contains(twozero)){
				    		textToReturn = textToReturn+onezero+"="+twozero+"###"+operationType.get(i);
				    		return textToReturn;
				    		
				    	}else if(!wordList.contains(onezero) && !wordList.contains(twozero)){
							if(tableKey.contains(onezero) && tableKey.contains(twozero)){
								ArrayList<Integer> object1 = indexOfAll(onezero,tableKey);
								ArrayList<Integer> object2 = indexOfAll(twozero,tableKey);
								
								String obOne = "";
								String obTwo = "";
								for(int x = 0 ; x < object1.size(); x++){
									int ttt = object1.get(x);
									tab = "llx_"+tableNames.get(object1.get(x)).toString();
									if(query.contains(tab)){
										obOne = obOne + tableNames.get(object1.get(x)).toString();
												break;
									}
								}
								for(int y = 0 ; y < object2.size();y++){
									int tt = object2.get(y);
									tab = "llx_"+tableNames.get(object2.get(y)).toString();
									if(query.contains(tab)){
										obTwo = obTwo + tableNames.get(object2.get(y)).toString();
												break;
									}
								}
								
								textToReturn = textToReturn + obOne+"="+obTwo+"###"+operationType.get(i);
								return textToReturn;
							}
						}else if(!wordList.contains(onezero) && wordList.contains(twozero)){
							if(tableKey.contains(onezero)){
								ArrayList<Integer> object1 = indexOfAll(onezero,tableKey);
				
								
								String obOne = "";
						
								for(int x = 0 ; x < object1.size(); x++){
									tab = "llx_"+tableNames.get(object1.get(x)).toString();
									if(query.contains(tab)){
										obOne = obOne + tableNames.get(object1.get(x)).toString();
												break;
									}
								}
								
								
								
								textToReturn = textToReturn+ obOne+"="+two[0]+"###"+operationType.get(i);
								return textToReturn;
								
							}
						}else if(wordList.contains(onezero) && !wordList.contains(twozero)){
							if(tableKey.contains(twozero)){
								ArrayList<Integer> object2 = indexOfAll(twozero,tableKey);
								
								
								String obTwo = "";
								
								for(int y = 0 ; y < object2.size();y++){
									tab = "llx_"+tableNames.get(object2.get(y)).toString();
									if(query.contains(tab)){
										obTwo = obTwo + tableNames.get(object2.get(y)).toString();
												break;
									}
								}
								
								
								textToReturn = textToReturn+one[0]+"="+obTwo+"###"+operationType.get(i);
								return textToReturn;
							}
						}
				    	
				    	
				    	//System.out.println(xx);
				    	
				    	}
		    	}
		    	
		    	
		    	
		    	
				
			}else{
				
				textToReturn = "nill";
				return textToReturn;
			}
			
			
		
		}
		
		return textToReturn;
		
		
	}
	
	
	
	
	//Get all the tables names in the database
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
	
	
	

	public static String  analyseNormalKeys(String tableData, List<List<String>> givenNameOfTables) {

				if (tableData.contains("SELECT")) {
					
					return idneitifyingThePatterns( tableData, "SELECT",givenNameOfTables);

				} else if (tableData.contains("UPDATE")) {
					System.out.println("Update");
					return idneitifyingThePatterns(tableData, "UPDATE",givenNameOfTables);

				} else if (tableData.contains("INSERT")) {
					System.out.println("Insert");
					return idneitifyingThePatterns( tableData, "INSERT",givenNameOfTables);

				} else if (tableData.contains("DELETE")) {
					System.out.println("Delete");
					return idneitifyingThePatterns( tableData, "DELETE",givenNameOfTables);
				}
				
				return "nill";

	}
	
	

	
	//Now there are two conditions we have to check here
	//1. Split the string using space
	//2. 
	public static String idneitifyingThePatterns(String splittedText, String opeationType, List<List<String>> givenNameOfTables) {

		String textToReturn = "";
		ArrayList tablesInDB = getDatabseTabelNames();
		
		
		ArrayList foriegnKeyGivenNames = (ArrayList)givenNameOfTables.get(0); //The given name
		ArrayList foriegnKeyTablesNames = (ArrayList)givenNameOfTables.get(1); //Acutal name in the database
		

		splittedText = splittedText.replaceAll("\\s+", " ").replace("\"", "");
		splittedText = splittedText.replaceAll("\\(", " ").replace("\\)", " ");
		String split[] =  splittedText.split(" ");
		
			for (int a = 0; a < split.length; a++) {
				String temp = split[a];
				//temp = temp.replaceAll("\\s+", "").replace("\"", "");
				String splittText[] = temp.split("\\.");
				if(splittText.length > 0){
					//So here we verify that the tables are the databases that we are actually using
					if(tablesInDB.contains(splittText[0])){
						splittText[0] = splittText[0].replace("llx_", "");
						textToReturn = textToReturn+splittText[0]+"###"+opeationType;
						return textToReturn;
					}else if(foriegnKeyGivenNames.contains(splittText[0]) && splittedText.contains("llx_"+foriegnKeyTablesNames.get(foriegnKeyGivenNames.indexOf(splittText[0])))){
						int index = foriegnKeyGivenNames.indexOf(splittText[0]);
						textToReturn = textToReturn+foriegnKeyTablesNames.get(index)+"###"+opeationType;
						return textToReturn;
						//However there some foriegn key relationships which are not in the original database naming convetions
						//As such we have to compare them using the additional information.
						
					}
					
					
				}
				
						
			}
		
		
		return textToReturn;
	}

	

}
