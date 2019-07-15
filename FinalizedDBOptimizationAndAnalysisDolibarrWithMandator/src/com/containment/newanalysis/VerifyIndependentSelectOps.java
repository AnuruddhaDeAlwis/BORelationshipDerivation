package com.containment.newanalysis;

import java.util.ArrayList;
import java.util.List;

public class VerifyIndependentSelectOps {
	/*
	 * From the Containmentverifiaction class we have to identify whether there are independet select operations related to the
	 * related BOs. If so the this calls will analyse and return the details.
	 * */
	
	
	public static boolean selectOperationAnalysis(String tab1, String tab2, List<List<String>> allNormalKeytablesDetials){
		
		ArrayList tables = (ArrayList)allNormalKeytablesDetials.get(0);
		ArrayList operations = (ArrayList)allNormalKeytablesDetials.get(1);
		
		if(tables.contains(tab1) && tables.contains(tab2)){
			if(operations.get(tables.indexOf(tab1)).toString().equalsIgnoreCase("SELECT") && operations.get(tables.indexOf(tab2)).toString().equalsIgnoreCase("SELECT")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
		
		
	}

}
