package de.tudarmstadt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExractData {

	public static void main(String[] args) {
		extactDataToJSON();
		extractDataToCSV();
	}

	private static void extractDataToCSV() {
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void extactDataToJSON() {
		try {
			List<String> commandList = new ArrayList<String>();
			commandList.add("sh");
			commandList.add("-c");
			commandList.add("python3 /home/sibgha/thesis-files/script.py");

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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
