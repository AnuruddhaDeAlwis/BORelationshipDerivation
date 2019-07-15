package com.table.clustering;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class EntropyCalculation {

	//Here we to calculte the entropy for all the tables. In order to do this we 
	//1. First will get identify the unique values related to each column in a table.
	//2. Then we will calculate te the log values for each attrubute based on that. 
	//3. The total of all attribute entropy will be the entropy of the table
	public static ArrayList allEntroupyValues = new ArrayList();
	
	
	public static  ArrayList calculatingTheEntropy(){
		
		
		ArrayList allTableNames = getDatabseTabelNames();
		for(int i=0; i<allTableNames.size();i++){
			ArrayList attributes = getAllAttributeNamesOfATable(allTableNames.get(i).toString());
			float logValue = 0.0f;
			for(int j=0;j<attributes.size();j++){
				
				ArrayList allDistinctValues = getDistinctValuesForAnAttribute(attributes.get(j).toString(),allTableNames.get(i).toString());
				ArrayList allValues = getAllValuesForAnAttribute(attributes.get(j).toString(),allTableNames.get(i).toString());
				
				
				
				allDistinctValues.removeAll(Collections.singleton(null));
				allValues.removeAll(Collections.singleton(null));
				
				int totalValuesCount =  allValues.size();
				//getting the count of each individual attribute repatition
				for(int x=0;x<allDistinctValues.size();x++){
					
					int countOfRepatition = 0;
					for(int k = 0; k<allValues.size();k++){
						if(allDistinctValues.get(x).toString().equalsIgnoreCase(allValues.get(k).toString())){
							countOfRepatition++;
						}
					}
					
					
						logValue = (float) (logValue + (float)countOfRepatition/totalValuesCount * Math.log((float)totalValuesCount/countOfRepatition));
					
				}
				
				
				
				
			}
			
			
			allEntroupyValues.add(logValue);
			
		}
		
		return allEntroupyValues;
		
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
	
	
	public static ArrayList getAllAttributeNamesOfATable(String table){
		ArrayList attributeNames = new ArrayList();
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/sugarcrm","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SHOW COLUMNS FROM "+table);  
			while(rs.next()) 
				//System.out.println(rs.getObject(1));
				attributeNames.add(rs.getObject(1)); 
				con.close();  
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return attributeNames;
	}
	
	
	public static ArrayList getDistinctValuesForAnAttribute(String attribute, String table){
		ArrayList values = new ArrayList();
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/sugarcrm","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT DISTINCT "+attribute+" FROM "+table);  
			while(rs.next()) 
				//System.out.println(rs.getObject(1));
				values.add(rs.getObject(1)); 
				con.close();  
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return values;
	}
	
	
	public static ArrayList getAllValuesForAnAttribute(String attribute, String table){
		ArrayList values = new ArrayList();
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/sugarcrm","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT "+attribute+" FROM "+table);  
			while(rs.next()) 
				//System.out.println(rs.getObject(1));
				values.add(rs.getObject(1)); 
				con.close();  
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return values;
	}
	
	
	
	
}
