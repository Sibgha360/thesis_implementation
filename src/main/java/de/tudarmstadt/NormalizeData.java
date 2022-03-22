package de.tudarmstadt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class NormalizeData {
	private final static String aliasFilePath = "/home/sibgha/thesis-files/aliases.csv";
//	private final static String aliasFilePath = "/home/sibgha/thesis-files/temp/alias.json";

	// create a map
	public static HashMap<String, List<String>> map = new HashMap<String, List<String>>();

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		copyAliasesInMemory();

		normalize();
	}

	private static void normalize() {

		map.forEach((alias, indicators) -> {

			// add alias in the alias table
			DbUtil.insertAlias(alias);

			// add it in the normalized table against the corresponding entries
			try {
				setAliasInNormalizedIndicator(alias, indicators);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private static void setAliasInNormalizedIndicator(String alias, List<String> indicators) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");

		String notRgx = indicators.get(0);
		indicators.remove(0);
		StringBuilder rgx = new StringBuilder();
		for (String ind : indicators) {
			try {
				if(ind.isBlank()|| ind.isEmpty())
				{
					continue;
				}
				rgx.append(" and indicator_name REGEXP ");
				rgx.append("'"+ind+"'");
		
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		try {
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

			Statement stmt = con.createStatement();
			String sql = "update normalized_indicator set alias = '" + alias
					+ "' where"
					+ " indicator_name NOT REGEXP '"+notRgx+"'"
					+ " " + rgx.toString()
					+ " and  normalized_indicator_id > 0";
			
			Boolean b = stmt.execute(sql);
					//+ " indicator_name like '" + ind + "' and  normalized_indicator_id > 0");

			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static void copyAliasesInMemory() throws IOException, ParseException, FileNotFoundException {
		JSONParser parser = new JSONParser();

		
		try (InputStream in = new FileInputStream(aliasFilePath);) {
		    CSV csv = new CSV(true, ',', in );
		    List < String > fieldNames = null;
		    if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());
		    List < Map < String, List<String> >> list = new ArrayList < > ();
		    while (csv.hasNext()) {
		        List < String > x = csv.next();
		        Map < String, List<String>> obj = new LinkedHashMap < > ();
		        
		        for (int i = 0; i < fieldNames.size(); i++) {
			        List<String> rgx = new ArrayList<String>();
			        List<String> temp = new ArrayList<String>();
			       
		            
		            for (int j = 0; j < x.size(); j++) {
			        	 rgx.add( x.get(j));
			        }
		            temp.add(rgx.get(0));
		            rgx.remove(0);
		            obj.put(fieldNames.get(0),  temp);
		            
		            
		            obj.put(fieldNames.get(1),  rgx);
		            
		            map.put(temp.get(0), rgx);
		            
		        }
		        list.add(obj);
		    }
		    ObjectMapper mapper = new ObjectMapper();
		    mapper.enable(SerializationFeature.INDENT_OUTPUT);
		    mapper.writeValue(System.out, list);
		}
		
//	System.out.println("");
//		
//		
//		Object jsonFileParsed = parser.parse(new FileReader(aliasFilePath));
//
//		JSONArray jsonArray = (JSONArray) jsonFileParsed;
//
//		jsonArray.forEach(object -> {
//
//			JSONObject jsonObject = (JSONObject) object;
//			
//			Set keySet = jsonObject.keySet();
//			Collection values = jsonObject.values();
//
//			Object object2 = values.toArray()[0];
//			Object object3 = values.toArray()[1];
//
//			Collection<String> coll = (Collection<String>) object2;
//			List<String> list = new ArrayList<String>(coll);
//
//			map.put((String) object3, list);
//		});
	}

}
