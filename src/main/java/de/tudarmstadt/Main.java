package de.tudarmstadt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		//convert the json into RDBMS
		storeData();
		
		parse();
		
		analyse();
	}

	private static void storeData() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	private static void parse() {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("/home/sibgha/thesis-files/adidas.json"));
 
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONArray jsonArray = (JSONArray) obj;
			
			jsonArray.forEach(object -> {
				System.out.println(object);
				insert((JSONObject)object);
			});
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private static void insert(JSONObject jsonIndicator) {
		Set keySet = jsonIndicator.keySet();
		Collection values = jsonIndicator.values();
		
	}

	@SuppressWarnings("unused")
	private static void initJson() throws InterruptedException {
		try {
			List<String> commandList = new ArrayList<String>();
			commandList.add("sh");
			commandList.add("-c");
			commandList.add("python3 /home/sibgha/thesis-files/script_csv.py");

			ProcessBuilder processBuilder = new ProcessBuilder(commandList);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.print(line + "\n");
			}

			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private static void analyse() {
		
	}
}
