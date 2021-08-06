package de.tudarmstadt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		parse();

		// convert the json into RDBMS
		storeData();

		analyse();
	}

	private static void storeData() {
		// TODO Auto-generated method sextractUnitextractUnittub

	}

	@SuppressWarnings("unchecked")
	private static void parse() {
		JSONParser parser = new JSONParser();
		try {
			int tableNumber = 1;
			String companyName = "adidas";

			// get company id from the database
			Integer companyId = DbUtil.getCompanyId(companyName);

			Object jsonFileParsed = parser
					.parse(new FileReader("/home/sibgha/thesis-files/" + companyName + tableNumber + ".json"));

			// A JSON object. Key value pairs are unordered. JSONObject supports
			// java.util.Map interface.
			JSONArray jsonArray = (JSONArray) jsonFileParsed;

			// find year indexes
			Map<String, Integer> yearIndexMap = extractYesrIndex(jsonArray);

			yearIndexMap.forEach((s, q) -> System.out.println(s + ": " + q));

			// unit
			String unit = extractUnit(jsonArray);
			System.out.println(unit);

			// currency
			String currecy = extractCurrency(jsonArray);
			System.out.println(currecy);

			yearIndexMap.forEach((year, yearIndex) -> {

				jsonArray.forEach(object -> {
					Set keySet = ((JSONObject) object).keySet();
					Collection values = ((JSONObject) object).values();

					Object[] valuesArray = values.toArray();
					Object[] KeyArray = keySet.toArray();

					String indicator = valuesArray[0].toString();

					String indicatorValue = valuesArray[yearIndex].toString().replace(",", ".");

					try {
						Double.valueOf(indicatorValue);
					} catch (NumberFormatException e) {
						return;
					}

					List<String> yearList = new ArrayList<String>();
					yearList.add("2021");
					yearList.add("2020");
					yearList.add("2019");
					yearList.add("2018");
					yearList.add("2017");
					yearList.add("2016");
					yearList.add("2015");
					yearList.add("2014");

					if (yearList.contains(indicatorValue)) {
						return;
					}

					if (indicator.isEmpty()) {
						return;
					}

					Double value = Double.valueOf(indicatorValue);

					DbUtil.insertIndicator(companyId, indicator, value, currecy, unit, year, tableNumber);
				});
			});

//			2021 index 0 2020 index 1 and so on
			List<Map<String, Integer>> listOfIndicators;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String extractUnit(JSONArray jsonArray) {
		List<String> unit = new ArrayList<String>();

		// find out out the unit
		jsonArray.forEach(object -> {
			Set keySet = ((JSONObject) object).keySet();
			Collection values = ((JSONObject) object).values();

			Object[] valuesArray = values.toArray();
			Object[] KeyArray = keySet.toArray();

			String extractUnit = getUnit(valuesArray);
			if (extractUnit == null) {
				return;
			}

			unit.add(extractUnit);
		});

		if (unit.size() > 0) {
			return unit.get(0);
		}
		return null;
	}

	private static String getUnit(Object[] valuesArray) {
		String unit = null;
		for (int n = 0; n < valuesArray.length; n++) {
			if (valuesArray[n].toString().contains("billion") || valuesArray[n].toString().contains("billions")) {
				unit = "B";
				return unit;
			}
			if (valuesArray[n].toString().contains("million") || valuesArray[n].toString().contains("millions")) {
				unit = "M";
				return unit;
			}
			if (valuesArray[n].toString().contains("thousand") || valuesArray[n].toString().contains("thousands")) {
				unit = "T";
				return unit;
			}
			if (valuesArray[n].toString().contains("hundred") || valuesArray[n].toString().contains("hundreds")) {
				unit = "H";
				return unit;
			}
		}
		return unit;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Integer> extractYesrIndex(JSONArray jsonArray) {
		Map<String, Integer> yearIndexMap = new HashMap<String, Integer>();
		jsonArray.forEach(object -> {
			System.out.println(object);
			Set keySet = ((JSONObject) object).keySet();
			Collection values = ((JSONObject) object).values();

			Object[] valuesArray = values.toArray();
			Object[] KeyArray = keySet.toArray();

			for (int n = 0; n < valuesArray.length; n++) {
				if (valuesArray[n].toString().contains("2021")) {
					yearIndexMap.put("2021", n);
				}
				if (valuesArray[n].toString().contains("2020")) {
					yearIndexMap.put("2020", n);
				}
				if (valuesArray[n].toString().contains("2019")) {
					yearIndexMap.put("2019", n);
				}
				if (valuesArray[n].toString().contains("2018")) {
					yearIndexMap.put("2018", n);
				}
				if (valuesArray[n].toString().contains("2017")) {
					yearIndexMap.put("2017", n);
				}
				if (valuesArray[n].toString().contains("2016")) {
					yearIndexMap.put("2016", n);
				}
				if (valuesArray[n].toString().contains("2015")) {
					yearIndexMap.put("2015", n);
				}
			}
		});

		return yearIndexMap;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private static void insert(JSONObject jsonIndicator) {
		Set keySet = jsonIndicator.keySet();
		Collection values = jsonIndicator.values();

		values.forEach(q -> System.out.print(q));
		System.out.println();

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

	@SuppressWarnings("unchecked")
	private static String extractCurrency(JSONArray jsonArray) {
		List<String> unit = new ArrayList<String>();

		// find out out the unit
		jsonArray.forEach(object -> {
			Set keySet = ((JSONObject) object).keySet();
			Collection values = ((JSONObject) object).values();

			Object[] valuesArray = values.toArray();
			Object[] KeyArray = keySet.toArray();

			String extractUnit = getCurrency(valuesArray);
			if (extractUnit == null) {
				return;
			}

			unit.add(extractUnit);
		});

		if (unit.size() > 0) {
			return unit.get(0);
		}
		return null;
	}

	private static String getCurrency(Object[] valuesArray) {
		String unit = null;
		for (int n = 0; n < valuesArray.length; n++) {
			if (valuesArray[n].toString().toLowerCase().contains("euro")
					|| valuesArray[n].toString().toLowerCase().contains("eur")
					|| valuesArray[n].toString().toLowerCase().contains("Euros")
					|| valuesArray[n].toString().contains("\u20AC") || valuesArray[n].toString().contains("€")) {
				unit = "EUR";
				return unit;
			}
			if (valuesArray[n].toString().toLowerCase().contains("pound")
					|| valuesArray[n].toString().toLowerCase().contains("pounds")
					|| valuesArray[n].toString().toLowerCase().contains("\u00a3")) {
				unit = "GBP";
				return unit;
			}
			if (valuesArray[n].toString().toLowerCase().contains("usd")
					|| valuesArray[n].toString().toLowerCase().contains("dollar")
					|| valuesArray[n].toString().toLowerCase().contains("dollars")) {
				unit = "USD";
				return unit;
			}
		}
		return unit;
	}

	private static void analyse() {

	}
}
