package de.tudarmstadt;

public class Main {
	public final static String baseLocation = "/Users/sibgha/Documents";
	public final static String aliasFilePath = "/Users/sibgha/Documents/aliases.csv";
	public static Double usdToEuroRate = 0.91D;

	public static void main(String[] args) {

		try {
			//
			//extract the pdf tables and saves the data in the json files.
			//(The script uses camelot api to extract the tables from the defined set of pdf reports. See /thesis-files/script.py)
			//
			//
			DataExtractionService.extactDataToJSON("thesis-files/script.py");

			//
			//get the data from json files and save it in the DB table indicator
			//
			DataProcessor.parseAndStore();

			//
			//take the data from the table indicator, save it in the normalized_indicator excluding the garbage rows
			//
			DataCleansingService.removeGarbageAndMoveIndicators();

			//assign aliases to the indicators and selects the one indicator for each company and year from the multiple occurances
			DataNormalizingService.copyAliasesInMemory(aliasFilePath);
			DataNormalizingService.asingAliases();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
