package com.containment.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.Program;

//This is the class where we are going to read all the code ralted to php and then extract the queries we need to get
public class StaticAnalysis {

	public static void extractTheQueries(String fileLocation, String locationSaveSQL) throws Exception {
		final File folderWithSugarCrmXmlFiles = new File(fileLocation);
		listFilesForFolder(folderWithSugarCrmXmlFiles, locationSaveSQL);
	}

	public static void listFilesForFolder(final File folder, String locationSaveSQL) throws Exception {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, locationSaveSQL);
			} else {

				String ext1 = FilenameUtils.getExtension(fileEntry.getAbsolutePath());
				if (ext1.equalsIgnoreCase("php")) {
					ArrayList textExtractedFromFile = new ArrayList();

					String fileInformation = FileUtils.readFileToString(new File(fileEntry.getAbsolutePath()), "UTF-8");

					Pattern p = Pattern.compile(
							"(INSERT INTO|UPDATE|SELECT|WITH|DELETE|FROM|JOIN).*?;",
							Pattern.DOTALL);
//					Pattern p = Pattern.compile(
//							"(INSERT INTO|UPDATE|SELECT|DELETE)+(\\W(?:[a-zA-Z'-_ ]+[a-zA-Z'-_ ]+[\\s\\S])+?)",
//							Pattern.DOTALL);

					Matcher m = p.matcher(fileInformation);

					// We have extracted all the sentences which starts with
					// INSERT INTO|UPDATE|SELECT|WITH|DELETE|FROM|JOIN.
					while (m.find()) {
                                         
                                            
						for (int a = 0; a < m.groupCount(); a++) {
							textExtractedFromFile.add(m.group(a));
						}
					}
					
					
					//Now we have to identify the sentences which contain the join operations and based on that create a new
					//arraylist and then save it as a text file for further processing
					ArrayList senetencesToWrite = new ArrayList();
					for(int i=0; i<textExtractedFromFile.size(); i++){
						String splitted[] = textExtractedFromFile.get(i).toString().split(" ");
						
						//If a sentence contains from or join operations concatinate it with the previouse sentence
						if(textExtractedFromFile.get(i).toString().contains("FROM") || textExtractedFromFile.get(i).toString().contains("JOIN")){
							
							if(senetencesToWrite.size() > 0){
								String tempText = senetencesToWrite.get(senetencesToWrite.size()-1).toString();
                                                                String concatinatedText = senetencesToWrite.get(senetencesToWrite.size()-1).toString() + textExtractedFromFile.get(i).toString();
                                                                senetencesToWrite.set(senetencesToWrite.size()-1, concatinatedText);
								
								
								
							}
							
						
						}else{
							senetencesToWrite.add(textExtractedFromFile.get(i));
						}
					}
					
					//Writing the SQL into a files for further processing
					writeTheDataToFiles(locationSaveSQL, FilenameUtils.removeExtension(fileEntry.getName()), senetencesToWrite);
					//writeTheDataToFiles(locationSaveSQL, FilenameUtils.removeExtension(fileEntry.getName()), textExtractedFromFile);
					
				}

			}
		}
	}
	
	
	
	public static void writeTheDataToFiles(String location, String filename, ArrayList senetencesToWrite){
		String locationToSave = location+"/"+filename;
		String textToBeWritten = "";
		for(int i=0; i<senetencesToWrite.size();i++){
			if(i == 0){
				textToBeWritten = textToBeWritten+senetencesToWrite.get(i);
			}else{
				textToBeWritten = textToBeWritten +"@@@"+senetencesToWrite.get(i);
			}
		}
		
		
		
		BufferedWriter writer;
		try {
			textToBeWritten = textToBeWritten.replace("\n", "").replace("\r", "");
			textToBeWritten = textToBeWritten.replaceAll("\\s+", " ");
			writer = new BufferedWriter(new FileWriter(locationToSave));
			writer.write(textToBeWritten);
		     
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
	}

}
