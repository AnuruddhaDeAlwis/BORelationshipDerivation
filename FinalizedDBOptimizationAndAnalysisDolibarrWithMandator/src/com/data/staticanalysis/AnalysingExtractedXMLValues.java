package com.data.staticanalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysingExtractedXMLValues {
	static String[] allTablesFrimSQL;
	static List<String> wordList;
	
		public static void identifyTablesFromQueries(String dataReadLocation, String DataWriteLocation, String extraInfoLocation){
			String allInfromation = "";
			String allTables = "";
			
			try {
			    BufferedReader in = new BufferedReader(new FileReader(dataReadLocation));
			    String str;
			    while ((str = in.readLine()) != null)
			    	allInfromation = allInfromation + str;
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
			String [] extraData = extraInfo.split("@@");	
			
			//The detials for the foriegn key tables which contians other information like eabr = email_addr_bean_rel
			HashMap<String, String> hmap = new HashMap<String, String>();
			
			for(int i=0; i< extraData.length; i++){
				String [] splittedData = extraData[i].split("=");
				hmap.put(splittedData[0], splittedData[1]);
			}
			
			
			//So here we get all the databse table names. If tha particular name is not there then there is no use of adding that 
			//particular value to the allTables.
			wordList = getDatabseTabelNames();
			
			Pattern p = Pattern.compile("((\\w)+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z_ ]+\\.+(\\w+))",Pattern.DOTALL); //Best with all
			//Pattern p = Pattern.compile("[a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+)", Pattern.DOTALL);
			
			//System.out.println("Log :"+fileEntry);
				
		    	Matcher m = p.matcher(allInfromation);
		    	
		    	while (m.find()) {
		    		String xx = "";
		    	for(int a=0;a<m.groupCount()-1;a++){
		    		//System.out.println("Value: "+m.group(a));
		    		xx = m.group(a);
		    	}
		    	
		    	String[] betweenEqual = xx.split("=");
		    	String[] one = betweenEqual[0].split("\\.");
		    	String[] two = betweenEqual[1].split("\\.");
		    	
		    	String onezero = one[0].replaceAll("\\s+","");
		    	String twozero = two[0].replaceAll("\\s+","");
		    	
		    	if(wordList.contains(onezero) && wordList.contains(twozero)){
		    		allTables = allTables +xx+"@@@";
		    	}else if(!wordList.contains(onezero) && !wordList.contains(twozero)){
					if(hmap.containsKey(onezero) && hmap.containsKey(twozero)){
						String datatoInsert = hmap.get(onezero)+"."+one[1]+"="+hmap.get(twozero)+"."+two[1];
						allTables = allTables +datatoInsert+"@@@";
					}
				}else if(!wordList.contains(onezero) && wordList.contains(twozero)){
					if(hmap.containsKey(onezero)){
						String datatoInsert = hmap.get(onezero)+"."+one[1]+"="+two[0]+"."+two[1];
						allTables = allTables +datatoInsert+"@@@";
					}
				}else if(wordList.contains(onezero) && !wordList.contains(twozero)){
					if(hmap.containsKey(twozero)){
						String datatoInsert = one[0]+"."+one[1]+"="+hmap.get(twozero)+"."+two[1];
						allTables = allTables +datatoInsert+"@@@";
					}
				}
		    	
		    	
		    	//System.out.println(xx);
		    	
		    	}
		    	
			
		    	
		    	
		    	
		 	    try {
		 	    	BufferedWriter writer = new BufferedWriter(new FileWriter(DataWriteLocation));
					writer.write(allTables);
				    writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 	   
		}
		
		
		
		public static ArrayList getDatabseTabelNames(){
			ArrayList allTableNames = new ArrayList();
			try{  
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/sugarcrm","root","");  
				//here sonoo is database name, root is username and password  
				Statement stmt=con.createStatement();  
				ResultSet rs=stmt.executeQuery("SHOW TABLES FROM sugarcrm");  
				while(rs.next())  
					allTableNames.add(rs.getObject(1)); 
					con.close();  
			}catch(Exception e){ 
				System.out.println(e);
				}  
			
			return allTableNames;
		}

}
