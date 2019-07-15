package com.data.xmlgenration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.data.queries.HandlingQueries;
import com.data.staticanalysis.AnalysingExtractedXMLValues;
import com.data.staticanalysis.ClusteringDatabaseTables;
import com.data.staticanalysis.WritingXMLExtractedValues;
import com.dynamic.analysis.BusinessObjectProcessing;
import com.dynamic.analysis.IdentifyExclusiveContainments;
import com.dynamic.analysis.IdentifyInclusiveContainment;
import com.dynamic.analysis.ProcessDynamicKeys;
import com.dynamic.analysis.ProcessLogFiles;
import com.dynamic.analysis.TestClass;
import com.evaluatesimilarity.givenclusters.InsertingTablesIntoFilteredClusters;
import com.evaluatesimilarity.givenclusters.SimilarityCalculationGivenClusters;
import com.jaccard.calculation.Cosine;
import com.jaccard.calculation.Jaccard;
import com.lowagie.toolbox.plugins.ExtractAttachments;
import com.mandoroptional.analysis.ProcessTheInputData;
import com.containment.analysis.AnalyseForiegnKeys;
import com.containment.analysis.AnalyzeRelatedBOS;
import com.containment.analysis.EvalauateExclusiveContainment;
import com.containment.analysis.EvaluatingInclusiveContainment;
import com.containment.analysis.ExtraInfoManage;
import com.containment.analysis.ForeignKeyAnalysis;
import com.containment.analysis.ReadingBOs;
import com.containment.analysis.StaticAnalysis;
import com.containment.analysis.TableAnalysis;
import com.containment.analysis.WritingTheStaticData;
import com.containment.newanalysis.ContainmentVerification;
import com.qgram.calculation.NGram;
import com.qgram.calculation.QGram;
import com.table.clustering.EntropyCalculation;
import com.table.clustering.TableClustering;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;



public class SimplePhpReader {
	public static void main(final String[] args) throws Exception {
		//This contains the extra information about the tables which have given different names than the one in the 
		//database in order to create the foreign key relaitonships
		List<List<String>> givenNameOfTables = ExtraInfoManage.extraInformationMapping("C:/DolibarrResulsts/ExtraInfo.txt");
		
	
		//This is the part related to the dynamic processing
		//List<List<String>> categorisedLogs = ProcessLogFiles.processLogFiles("C:/DolibarrResulsts/deleteCustomerOrder.log");
		List<List<String>> categorisedLogs = ProcessLogFiles.processLogFiles("C:/DolibarrResulsts/deletePurchaseOrder.log");
		//List<List<String>> categorisedLogs2 = TestClass.readAndProcessData("C:/DolibarrResulsts/test.txt");
		List<List<String>> operationsAndTables = ProcessDynamicKeys.processDynamicKeyInformation(categorisedLogs, givenNameOfTables);
		
		//Identify the tables related to each BO
		List<List<String>> allTheBOTables = BusinessObjectProcessing.readindTheBusinessObjectsTables("C:/DolibarrResulsts/BusinessObjects.txt");
		
		//The name of the business objects
		ArrayList allTheBOs = BusinessObjectProcessing.readindTheBusinessObjects("C:/DolibarrResulsts/BusinessObjects.txt");
		List<List<String>> exclusivelyContained = IdentifyExclusiveContainments.procesContainment(operationsAndTables, allTheBOTables, allTheBOs);	
		List<List<String>> inclusivelyContained = IdentifyInclusiveContainment.procesContainment(operationsAndTables, allTheBOTables, allTheBOs, exclusivelyContained);	
		
		
		
		
		//The optional and mandatory evaluations
		ProcessTheInputData.processingTheDataFile("C:/DolibarrResulsts/TheTableDetails.txt", operationsAndTables,"ecm","orders");
		System.out.println("Test");
		
		
	}
	
	
	public static void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            //System.out.println(fileEntry.getName());
	            //System.out.println(fileEntry.getAbsolutePath());
	            final ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1);
	            String ext1 = FilenameUtils.getExtension(fileEntry.getAbsolutePath());
	            if(ext1.equalsIgnoreCase("php")){
	            	parser.setSource(FileUtils.readFileToString(
	            			new File(fileEntry.getAbsolutePath()), "UTF-8").toCharArray());
	            	final Program node = parser.createAST(null);
	            	
	            	try{
	            		//This is if we need to get the individual eliments in an xml document
	            		final SimpleVisitor visitor = new SimpleVisitor();
	            		String fileNameWithOutExt = FilenameUtils.removeExtension(fileEntry.getName());
	            		visitor.classNameForTheFile(fileNameWithOutExt);
	            		node.accept(visitor);
	            	}catch(Exception e){
	            		
	            	}

	            	
	            }
	            
	            
	        }
	    }
	}

	
	
	
	public static void printValues(List<List<String>> clusteringGroups){
		
		for(int i=0;i<clusteringGroups.size();i++){
			ArrayList names= (ArrayList) clusteringGroups.get(i);
			
			for(int j=0;j<names.size();j++){
				System.out.print(names.get(j)+", ");
			}
			System.out.println();
			System.out.println("--------------------------------------------------------------------------------");
		}
		
	}
	

	
}
