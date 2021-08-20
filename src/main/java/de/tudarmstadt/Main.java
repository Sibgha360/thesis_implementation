package de.tudarmstadt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Sibgha
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		
//		ExractData.extactDataToJSON();
		
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
		int reportNumber = 1;

		int tableNumber = 3;
		String companyName = "puma";
		
		
		try {
			File folder = new File("/home/sibgha/thesis-files/json/astra");
		 
			 // Creating a filter to return only files.
            FileFilter fileFilter = new FileFilter()
            {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            };
            
			File[] listOfFiles = folder.listFiles(fileFilter);

            Arrays.sort(listOfFiles);

			for (int i = 0; i < listOfFiles.length; i++) {
				String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile()) {
					{
						filename = FilenameUtils.removeExtension(filename);

						Pattern p = Pattern.compile("\\d+");
						Matcher m = p.matcher(filename);
						while (m.find())
							tableNumber = Integer.valueOf(m.group());

						Pattern p1 = Pattern.compile("([A-Za-z]+)");
						Matcher m1 = p1.matcher(filename);
						while (m1.find())
							companyName = m1.group();
						if (tableNumber > 291) {
							int u = 0;
							int ii = u;
						}
						saveFileDataToMysql(reportNumber, tableNumber, companyName);
//					System.out.println(companyName+tableNumber);
					}
				}
				// safe check
				else if (listOfFiles[i].isDirectory()) {
					System.out.println(
							"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx  Directory " + filename);
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveFileDataToMysql( int reportNumber, int tableNumber, String companyName) throws FileNotFoundException, IOException, ParseException
			 {

		final List<String> contextForLogging = new ArrayList<String>();
			// get company id from the database
			Integer companyId = DbUtil.getCompanyId(companyName);

			JSONParser parser = new JSONParser();

			Object jsonFileParsed = parser
					.parse(new FileReader("/home/sibgha/thesis-files/json/" + companyName + tableNumber + ".json"));

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
			
			if(currecy==null)
			{
				return;
			}

			int o=0;
			
			List<Integer> firstValidIndicatorRowIndex = new ArrayList<Integer>();
			
			yearIndexMap.forEach((year, yearIndex) -> {
				IntStream.range(0, jsonArray.size()).forEach(idx -> {
					Object object = jsonArray.get(idx);
					
					
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
					

					if(firstValidIndicatorRowIndex.size()<=0) {
						firstValidIndicatorRowIndex.add(idx);
					}
					
					final Integer maxIndexContext = firstValidIndicatorRowIndex.get(0);

					Double value = Double.valueOf(indicatorValue);

					// everything above firstValidIndicatorRowIndex is the context

					String context = extractContext(jsonArray, maxIndexContext);
					contextForLogging.add(context);

					try {
						DbUtil.insertIndicator(companyId, indicator, value, currecy, unit, year, tableNumber, context, reportNumber);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						System.err.println(e.toString());
						String ctx = (contextForLogging.size() > 0) ? contextForLogging.get(0) : "no context";
						System.err.println(companyName+ ", " +tableNumber+", "+ctx);
						e.printStackTrace();
					}
				});				
				
//				jsonArray.forEach(object ->
//									});
			});
		 
//			2021 index 0 2020 index 1 and so on
		List<Map<String, Integer>> listOfIndicators;
	}

	@SuppressWarnings("unchecked")
	private static String extractContext(JSONArray jsonArray, final Integer maxIndexContext) {
		StringBuilder b = new StringBuilder();
		for (int n = 0; n < maxIndexContext-1; n++) {
			Object obj = jsonArray.get(n);

			b.append(((JSONObject) obj).values().stream().map(Object::toString).filter(x -> !x.toString().isEmpty()).collect(Collectors.joining(" ")).toString());
			b.append(" ");
		}
		
		return b.toString();
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
		String unit = "NA";
		for (int n = 0; n < valuesArray.length; n++) {
			String string = valuesArray[n].toString();
			if (string.contains("billion") || string.contains("billions") || (isCurrencySign(string) && (string.contains("b")))) {
				unit = "B";
				return unit;
			}
			if (string.contains("million") || string.contains("millions") || (isCurrencySign(string) && (string.contains("m")))) {
				unit = "M";
				return unit;
			}
			if (string.contains("thousand") || string.contains("thousands") || (isCurrencySign(string) && (string.contains("th")))) {
				unit = "TH";
				return unit;
			}
			if (string.contains("hundred") || string.contains("hundreds") || (isCurrencySign(string) && (string.contains("h")))) {
				unit = "H";
				return unit;
			}
		}
		return unit;
	}

	private static boolean isCurrencySign(String string) {
		return containsEuroSign(string) || containsDollarSign(string) || containsPoundSign(string);
	}

	private static boolean containsEuroSign(String string) {
		return string.toLowerCase().contains("euros") || string.toLowerCase().contains("euro") || string.contains("$") || string.contains("\u0024");
	}
	private static boolean containsDollarSign(String string) {
		return string.toLowerCase().contains("usd") || string.toLowerCase().contains("dollars") || string.toLowerCase().contains("dollar") || string.contains("$") || string.contains("\\u20AC");
	}
	private static boolean containsPoundSign(String string) {
		return string.toLowerCase().contains("pound") || string.toLowerCase().contains("pounds") || string.contains("£") || string.contains("\u00a3");
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
					|| valuesArray[n].toString().toLowerCase().contains("\u00a3")
					|| valuesArray[n].toString().toLowerCase().contains("£")) {
				unit = "GBP";
				return unit;
			}
			if (containsDollarSign(valuesArray[n].toString())) {
				unit = "USD";
				return unit;
			}
		}
		return unit;
	}

	private static void analyse() {

	}
}
