package de.tudarmstadt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Sibgha
 *
 */
public class DataExtractionService {

	/**
	 * for the purpose of testing the single class
	 * @param args
	 */
	public static void main(String[] args) {
		extactDataToJSON("/home/sibgha/thesis-files/script.py");
	}

	/**
	 * creates JSON files against the company financial reports table data. This is a one time execution but future work, condition can be added around it
	 * so it only executes if it already hasnt been yet.
	 * @param pythonScript
	 */
	public static void extactDataToJSON(String pythonScript) {
		try {
			List<String> commandList = new ArrayList<String>();
			commandList.add("sh");
			commandList.add("-c");
			commandList.add("python3 "+ pythonScript);

			ProcessBuilder processBuilder = new ProcessBuilder(commandList);
			processBuilder.redirectErrorStream(true);
			processBuilder.redirectOutput();

			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.print(line + "\n");
			}

			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
//
//	private static void extractDataToCSV() {
//		try {
//			List<String> commandList = new ArrayList<String>();
//			commandList.add("sh");
//			commandList.add("-c");
//			commandList.add("python3 /home/sibgha/thesis-files/script_csv.py");
//
//			ProcessBuilder processBuilder = new ProcessBuilder(commandList);
//			processBuilder.redirectErrorStream(true);
//			Process process = processBuilder.start();
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//			String line = "";
//			while ((line = reader.readLine()) != null) {
//				System.out.print(line + "\n");
//			}
//
//			process.waitFor();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


}
