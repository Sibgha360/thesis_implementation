package de.tudarmstadt;

import de.tudarmstadt.service.DataCleansingService;
import de.tudarmstadt.service.DataExtractionService;
import de.tudarmstadt.service.DataNormalizingService;
import de.tudarmstadt.service.DataProcessor;

public class Main {
	public final static String baseLocation = "/Users/sibgha/Documents";
	public final static String aliasFilePath = "/Users/sibgha/Documents/aliases.csv";
	public static Double usdToEuroRate = 0.91D;

	public static void main(String[] args) {

		try {
			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////  1. DataExtractionService  ///////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			//extract the pdf tables and saves the data in the json files.
			//(The script uses camelot api to extract the tables from the defined set of pdf reports. See /thesis-files/script.py)
			//
			//
			DataExtractionService.extactDataToJSON("/Users/sibgha/Documents/script.py");

			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////  2. DataProcessor    //////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			//get the data from json files and save it in the DB table indicator
			//
			DataProcessor.parseAndStore();

			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////  3. DataCleansingService  ///////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			//take the data from the table indicator, save it in the normalized_indicator excluding the garbage rows
			//
			DataCleansingService.removeGarbageAndMoveIndicators();

			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////  4. DataNormalizingService  ///////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//
			//assign aliases to the indicators and selects the one indicator for each company and year from the multiple occurrences
			//
			DataNormalizingService.copyAliasesInMemory(aliasFilePath);
			DataNormalizingService.assignAliases();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
