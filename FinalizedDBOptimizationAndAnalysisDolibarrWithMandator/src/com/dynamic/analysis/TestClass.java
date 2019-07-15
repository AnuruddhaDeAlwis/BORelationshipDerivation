package com.dynamic.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

	public static List<List<String>> readAndProcessData(String path){
		
	List<List<String>> givenNameOfTables = new ArrayList<List<String>>();
			
			String allTheBos = "";
			
			try {
	
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
	
				String st;
				while ((st = br.readLine()) != null) {
					allTheBos = allTheBos + st;
				}
	
			} catch (IOException e) {
	
				e.printStackTrace();
	
			}
			
			String split[] = allTheBos.split("@@@");
			
			ArrayList temp = new ArrayList();
			for(int i = 0; i < split.length; i++){
				temp.add(split[i]);
			}
			
			givenNameOfTables.add(temp);
			return givenNameOfTables;
			
	}
	
}
