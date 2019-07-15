package com.containment.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtraInfoManage {

	
	//This is the class which manage the extra infromation that we need for the BO idnetification
	//Since some tables use foriegn keys which are not related it need the table names to be mapped into that particular word.
	//For this we will use two arraylist. One with the real table name and the other with the named assigned
	
	public static List<List<String>> extraInformationMapping(String path){
		
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
		
		
		
		String word[] = allTheBos.split("@@@");
		ArrayList givenName = new ArrayList();
		ArrayList realName = new ArrayList();
		
		for(int i = 0; i < word.length; i++){
			String splitting[] = word[i].split("=");
			givenName.add(splitting[1]);
			realName.add(splitting[0]);
		}
		
		givenNameOfTables.add(givenName);
		givenNameOfTables.add(realName);
	
		return givenNameOfTables;
	}
}
