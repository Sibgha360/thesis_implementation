package de.tudarmstadt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUtil {

	public static String password = "root";
	public static String user = "root";
	public static String connUrl = "jdbc:mysql://localhost:3306/urdb?autoReconnect=true&useSSL=false";



	public static void main(String args[]) {

	}

	public static void insertIndicator(Integer companyId, String indicator, Double value, String currency, String unit,
			String year, int tableNumber, String context, int reportNumber, String pdfPath) throws Throwable {
		Class.forName("com.mysql.jdbc.Driver");

		Connection con = DriverManager.getConnection(connUrl,
				user, password);
		// the mysql insert statement
		String query = " insert ignore into indicator (company_id, indicator_name, value, unit, currency, year, table_number, report_number, context)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt = con.prepareStatement(query);
		preparedStmt.setInt(1, companyId);
		preparedStmt.setString(2, indicator);
		preparedStmt.setDouble(3, value);
		preparedStmt.setString(4, unit);
		preparedStmt.setString(5, currency);
		preparedStmt.setString(6, year);
		preparedStmt.setInt(7, tableNumber);
		preparedStmt.setInt(8, reportNumber);
		preparedStmt.setString(9, context);

		// execute the preparedstatement
		preparedStmt.execute();

		con.close();
	}

	public static Integer getCompanyId(String companyName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					connUrl, user, password);

			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("select company_id from company where company_name = '" + companyName + "'");

			rs.next();

			int companyId = rs.getInt("company_id");

			con.close();

			return companyId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Integer> getCompanies() {
		List<Integer> companyIds = new ArrayList<Integer>();
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					connUrl, user, password);

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select company_id from company");

			while (rs.next()) {
				int companyId = rs.getInt("company_id");
				companyIds.add(companyId);
			}

			con.close();

			return companyIds;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Report> getReports() {
		List<Integer> companyIds = new ArrayList<Integer>();
		List<Report> reports = new ArrayList<Report>();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					connUrl, user, password);

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select report.company_id, report_id, report_uri , company.company_name from report JOIN company on company.company_id = report.company_id where status = 0");

			while (rs.next()) {
				int companyId = rs.getInt("company_id");
				int reportId = rs.getInt("report_id");
				String reportUri = rs.getString("report_uri");
				String companyName = rs.getString("company_name");

				reports.add(new Report(companyId, reportId, reportUri, companyName));
			}

			con.close();

			return reports;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void updateReportStatustoExecuted(Integer reportId) {
		List<Integer> companyIds = new ArrayList<Integer>();
		List<Report> reports = new ArrayList<Report>();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					connUrl, user, password);

			Statement stmt = con.createStatement();
			Boolean b = stmt.execute("update report  SET report.status = 1 where report_id = "+ reportId.toString());
 
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertAlias(String alias) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(connUrl,
					user, password);

			// the mysql insert statement
			String query = " insert ignore into alias (alias)"
					+ " values (?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString(1, alias);

			// execute the preparedstatement
			preparedStmt.execute();

			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static Integer getSelectedAlias(String alias, Integer companyId, String year) {

		Integer normalizedIndicatorId = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					connUrl, user, password);

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select normalized_indicator_id from normalized_indicator where alias = '" + alias + "' and company_id = '" + companyId + "' and  year = '"  + year + "' and selected = 1");

			while (rs.next()) {
				normalizedIndicatorId = rs.getInt("normalized_indicator_id");
			}

			con.close();

			return normalizedIndicatorId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static double calculateValue(String unit, Double value, String currency) {
		
		if(unit.equalsIgnoreCase("h"))
		{
			return value * 100;
		}
		if(unit.equalsIgnoreCase("th"))
		{
			return value * 1000;
		}
		if(unit.equalsIgnoreCase("m"))
		{
			return value * 1000000;
		}
		if(unit.equalsIgnoreCase("b"))
		{
			return value * 100000000;
		}


		//add currency factor. convert to euro
		if(currency.equalsIgnoreCase("usd"))
		{
			return value * Main.usdToEuroRate;
		}
		else{
			return value;
		}
	} 
}
