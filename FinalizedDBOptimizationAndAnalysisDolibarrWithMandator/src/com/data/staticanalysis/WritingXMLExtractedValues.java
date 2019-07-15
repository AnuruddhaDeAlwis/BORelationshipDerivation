package com.data.staticanalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class WritingXMLExtractedValues {
	static String dataInTheFile = "";
	public static void listFilesForStaticSql(final File folder, String fileToSave) throws Exception {
		
		
		FileReader freader = new FileReader(fileToSave);
		BufferedReader br = new BufferedReader(freader);
		String s;
		while((s = br.readLine()) != null) {
			dataInTheFile = dataInTheFile + s;
		}
		freader.close();
		
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	listFilesForStaticSql(fileEntry,fileToSave);
	        } else {
	            //System.out.println(fileEntry.getName());
	            //System.out.println(fileEntry.getAbsolutePath());
	            String ext1 = FilenameUtils.getExtension(fileEntry.getAbsolutePath());
	            if(ext1.equalsIgnoreCase("xml")){
	            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	                DocumentBuilder db = dbf.newDocumentBuilder();
	            	Document document = db.parse(new File(fileEntry.getAbsolutePath().toString()));
	         
	      		  
	      		 NodeList nodeList = document.getElementsByTagName("Scalar");
	             for(int x=0,size= nodeList.getLength(); x<size; x++) {
	                 //System.out.print(nodeList.item(x).getAttributes().getNamedItem("value").getNodeValue());
	                 dataInTheFile = dataInTheFile + nodeList.item(x).getAttributes().getNamedItem("value").getNodeValue();
	             }
	            	
	            	
            }
	            
	            
	        }
	    }
	    
	    
	    BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
	    writer.write(dataInTheFile);
	    writer.close();
	    
	}
}
