package com.containment.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;

public class TableAnalysis {
	/*
	 * In this class we are going to read all the files we have created in
	 * staticAnalysis and then identify the tables which have relationships and
	 * also identify the tables which does not have the relationships. Later on
	 * they can be used to evaluate the inclusive and excusive containment.
	 */
	static ArrayList normalKeyTables = new ArrayList();
	static ArrayList operationType = new ArrayList();
	
	static List<List<String>> additionalInformation = new ArrayList<List<String>>();

	public static List<List<String>> tableAnalysis(String fileLocation, List<List<String>> extraInformation) throws Exception {
		
		additionalInformation = extraInformation;
		List<List<String>> allDetails = new ArrayList<List<String>>();
		
		   String textMappingTables = "";
	        try {
	            File file = new File(fileLocation);
	            
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String st;
	            while ((st = br.readLine()) != null) 
	                textMappingTables = textMappingTables + st+"@@@";
	        } catch (IOException ex) {
	           System.out.println("Exception: "+ex);
	        }
		
		
	        analysingTheQueries(textMappingTables);
	
		
		allDetails.add(normalKeyTables);
		allDetails.add(operationType);
		
		return allDetails;
		
	}

	

	public static void analysingTheQueries(String tableData) {

		String spiltString[] = tableData.split("@@@");

		for (int i = 0; i < spiltString.length; i++) {

			Pattern p = Pattern.compile("[a-zA-Z0-9]+\\.+[a-zA-Z_ ]+\\=+[a-zA-Z0-9_ ]+\\.+(\\w+)", Pattern.DOTALL);

			 Matcher m = p.matcher(spiltString[i]);
			

			// First we check whether there are any foriegn key relationshps in
			// the given query.
			// If it contains foriegn key relationships then we seperate them
			// into one String
			// Else we check the individual database tables and then extract the
			// database tables
			if (m.find()) {


			} else {

				if (spiltString[i].contains("SELECT")) {

					Pattern p1 = Pattern.compile("(MAIN_DB_PREFIX)+\\b((?:\\W*\\w+){1})", Pattern.DOTALL);
					idneitifyingThePatterns(p1, spiltString[i], "SELECT");

				} else if (spiltString[i].contains("UPDATE")) {

					Pattern p1 = Pattern.compile("(MAIN_DB_PREFIX)+\\b((?:\\W*\\w+){1})", Pattern.DOTALL);
					idneitifyingThePatterns(p1, spiltString[i], "UPDATE");

				} else if (spiltString[i].contains("INSERT")) {

					Pattern p1 = Pattern.compile("(MAIN_DB_PREFIX)+\\b((?:\\W*\\w+){1})", Pattern.DOTALL);
					idneitifyingThePatterns(p1, spiltString[i], "INSERT");

				} else if (spiltString[i].contains("DELETE")) {

					Pattern p1 = Pattern.compile("(MAIN_DB_PREFIX)+\\b((?:\\W*\\w+){1})", Pattern.DOTALL);
					idneitifyingThePatterns(p1, spiltString[i], "DELETE");
				}

			}

		}

	}

	public static void idneitifyingThePatterns(Pattern p, String splittedText, String opeationType) {

		ArrayList tablesInDB = getDatabseTabelNames();
		
		for(int i =0; i< tablesInDB.size(); i++){
            String nameOftable = tablesInDB.get(i).toString().replace("llx_", "");
            tablesInDB.set(i, nameOftable);
        }
		
		
		Matcher m1 = p.matcher(splittedText);
		
		ArrayList foriegnKeyGivenNames = (ArrayList)additionalInformation.get(0); //The given name
		ArrayList foriegnKeyTablesNames = (ArrayList)additionalInformation.get(1); //Acutal name in the database
		

		while (m1.find()) {
			for (int a = 0; a < m1.groupCount() - 1; a++) {
				String temp = m1.group(a);
				temp = temp.replaceAll("\\s+", "").replace("\"", "");
				String splittText[] = temp.split("\\.");
				if(splittText.length > 1){
					//So here we verify that the tables are the databases that we are actually using
					if(tablesInDB.contains(splittText[1])){
						normalKeyTables.add(splittText[1]);
						operationType.add(opeationType);
					}else if(foriegnKeyGivenNames.contains(splittText[1])){
						int index = foriegnKeyGivenNames.indexOf(splittText[1]);
						normalKeyTables.add(foriegnKeyTablesNames.get(index));
						operationType.add(opeationType);
						//However there some foriegn key relationships which are not in the original database naming convetions
						//As such we have to compare them using the additional information.
						
					}
					
					
				}
				
						
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
}
