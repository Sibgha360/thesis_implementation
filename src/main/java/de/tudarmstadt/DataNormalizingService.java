package de.tudarmstadt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DataNormalizingService {

	// a map to keep aliases in the memory
	public static HashMap<String, List<String>> map = new HashMap<String, List<String>>();

	public static void assignAliases() {

		map.forEach((alias, indicators) -> {

			// add alias in the alias table
			DbUtil.insertAlias(alias);

			// add it in the normalized table against the corresponding entries
			try {
				setAliasInNormalizedIndicator(alias, indicators);
			} catch (ClassNotFoundException e) {
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
				if(ind.isEmpty())
				{
					continue;
				}
				rgx.append(" and indicator_name REGEXP ");
				rgx.append("'"+ind+"'");
		
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		try{
		Class.forName("com.mysql.jdbc.Driver");

		Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "root", "root");

		Statement stmt = con.createStatement();
			String sql = "select normalized_indicator_id, company_id, year from normalized_indicator where"
					+ " indicator_name NOT REGEXP '"+notRgx+"'"
					+ " " + rgx.toString()
					+ " and  normalized_indicator_id > 0";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int normalizedIndicatorId = rs.getInt("normalized_indicator_id");
			int companyId = rs.getInt("company_id");
			String year = rs.getString("year");

			if(DbUtil.getSelectedAlias(alias, companyId, year) != null)
			{
				updateAlias(alias, notRgx, rgx);
			}
			else
			{
				updateAliasWithSelection(alias, notRgx, rgx, companyId, year);
			}
		}

		con.close();}
		catch (Throwable t)
		{
			t.printStackTrace();
		}





	}

	public static void updateAliasWithSelection(String alias, String notRgx, StringBuilder rgx, int companyId, String year) {
		updateAlias(alias, notRgx, rgx);

		//update selection flag
		try {
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "root", "root");


			Statement stmt = con.createStatement();
			String sql = "update normalized_indicator set selected = 1 where company_id = "+companyId+" and alias = "+alias+" and year = "+year+" ORDER BY value DESC limit 1";

			Boolean b = stmt.execute(sql);
			//+ " indicator_name like '" + ind + "' and  normalized_indicator_id > 0");

			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	private static void updateAlias(String alias, String notRgx, StringBuilder rgx) {
		try {
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "root", "root");


			Statement stmt = con.createStatement();
			String sql = "update normalized_indicator set alias = '" + alias
					+ "' where"
					+ " indicator_name NOT REGEXP '"+ notRgx +"'"
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

	public static void copyAliasesInMemory(String aliasFilePath) throws IOException, ParseException, FileNotFoundException {
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

	/**
	 * for testing purpose
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		copyAliasesInMemory(Main.aliasFilePath);
		assignAliases();
	}

}
