package com.data.queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class HandlingQueries {

	
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
	
	
	
	public static ArrayList getValuesForGivenAttribute(String table, String attribute, int count){
		
		ArrayList values = new ArrayList();
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/sugarcrm","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT DISTINCT "+attribute+" FROM "+table+" LIMIT 0,"+count);  
			while(rs.next()) 
				//System.out.println(rs.getObject(1));
				values.add(rs.getObject(1)); 
				con.close();  
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return values;
	}
	
	
	
public static String getAllValuesOfTabel(String table){
		
		String data = "";
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/sugarcrm","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT *  FROM "+table);  
			while(rs.next()) 
				//System.out.println(rs.getObject(1));
				data = data + rs.getObject(1).toString();
				con.close();  
		}catch(Exception e){ 
			System.out.println(e);
			}
				
		return data;
			 
		
		
	}



public static ArrayList getAllIdValuesFromTable(String id, String table){
	
	ArrayList data = new ArrayList();
	
	try{  
		Class.forName("com.mysql.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost:3306/sugarcrm","root","");  
		//here sonoo is database name, root is username and password  
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("SELECT "+id+" FROM "+table);  
		while(rs.next()) 
			//System.out.println(rs.getObject(1));
			data.add(rs.getObject(1).toString());
			con.close();  
	}catch(Exception e){ 
		System.out.println(e);
		}
			
	return data;
		 
	
	
}





public static List<List<String>> getTheTableSchema(String table){
	
    
	List<List<String>> allDetails = new ArrayList<List<String>>();
	ArrayList columns = new ArrayList();
	ArrayList pKeys = new ArrayList();
	
	String cloumnNames = "";
	
	try{  
		Class.forName("com.mysql.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost:3306/sugarcrm","root","");  
		//here sonoo is database name, root is username and password  
		
		int primaryKey = 0;
		DatabaseMetaData meta = con.getMetaData();
	      ResultSet res = meta.getColumns(null, null, table, null);
	      System.out.println("List of columns: "); 
	      while (res.next()) {
	    	  cloumnNames = cloumnNames +" "+res.getString("COLUMN_NAME");
	         System.out.println(
	           "  "+res.getString("TABLE_SCHEM")
	           + ", "+res.getString("TABLE_NAME")
	           + ", "+res.getString("COLUMN_NAME")
	           + ", "+res.getString("TYPE_NAME")
	           + ", "+res.getInt("COLUMN_SIZE")
	           + ", "+res.getInt("NULLABLE")); 
	         
	         columns.add(cloumnNames);
	         
	         String catalog = res.getString("TABLE_CAT");
	         String schema = res.getString("TABLE_SCHEM");
	         String tableName = res.getString("TABLE_NAME");
	         
	         try (ResultSet primaryKeys = meta.getPrimaryKeys(catalog, schema, tableName)) {
	             while (primaryKeys.next()) {
	            	 
	                 String pkey = primaryKeys.getString("COLUMN_NAME");
	                 if(!pKeys.contains(pkey)){
	                	 pKeys.add(pkey);
	                 }
	                 
	                 System.out.println("primary key: "+pkey);
	             }
	         }
	         
	         cloumnNames = "";
	      }
	      
	      
	      res.close();

	      con.close();
		  
	}catch(Exception e){ 
		System.out.println(e);
		}
	
	allDetails.add(pKeys);
	allDetails.add(columns);
	
			
	return allDetails;
		 
	
	
}
	
	
}
