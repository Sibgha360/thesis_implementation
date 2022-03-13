package de.tudarmstadt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DbUtil {
	public static void main(String args[]) {

	}

	public static void insertIndicator(Integer companyId, String indicator, Double value, String currency, String unit,
			String year, int tableNumber, String context, int reportNumber) throws Throwable {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false",
				"sibgha", "1234asdf");
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
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

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
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

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
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

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
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

			Statement stmt = con.createStatement();
			Boolean b = stmt.execute("update report  SET report.status = 1 where report_id = "+ reportId.toString());
 
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 	//1: 
		//
		//normalize the data by removing the duplications and unit information must be reflected by the indicator value. For example:  
		//sales: 13M
		//sales: 13M
		//
		//Normalized form: 
		//sales: 13000000
		//
		//2:
		//
		//remove indicators with numbers and symbols
		//
		//e.g. 
		//25%
		//2019 - 2020
		//()
	 */
	//TODO handlw the amounts with dollars and brackets PLEASE
	//TODO remove of which
	public static void getAlphaIndicators() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select   `indicator_name`, `indicator_id`, `company_id`, `year`, `currency`, `value`, table_number, report_number, context, unit FROM  indicator "
					+ "where  (indicator_name   NOT REGEXP  '^[^A-Za-z]' OR indicator_name    REGEXP   '^– +[A-Za-z]' OR indicator_name REGEXP '^• +[A-Za-z]' OR indicator_name    REGEXP   '^•+[A-Za-z]' )");
			
			// REGEXP '[a-z]' AND indicator_name   REGEXP '[A-Z]' AND indicator_name not REGEXP '[0-9]'

			while (rs.next()) {
				try {
					String indicator = rs.getString("indicator_name");
					int indicatorId = rs.getInt("indicator_id");
					int companyId = rs.getInt("company_id");
					String year = rs.getString("year");
					String currency = rs.getString("currency");
					Double value = rs.getDouble("value");
					int tableNumber = rs.getInt("table_number");
					int reportNumber = rs.getInt("report_number");
					String context = rs.getString("context");
					String unit = rs.getString("unit");
					
					String sql = "INSERT IGNORE INTO `mydb`.`normalized_indicator` (`indicator_name`, `indicator_id`, `company_id`, `year`, `currency`, `value`, table_number, report_number, context) VALUES (?,?,?,?,?,?,?,?,?)";
					
					// create the mysql insert preparedstatement
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, indicator);
					preparedStmt.setInt(2, indicatorId);
					preparedStmt.setInt(3, companyId);
					preparedStmt.setString(4, year);
					preparedStmt.setString(5, currency);
					preparedStmt.setDouble(6, calculateValue(unit, value));
					preparedStmt.setInt(7, tableNumber);
					preparedStmt.setInt(8, reportNumber);
					preparedStmt.setString(9, context);

					preparedStmt.execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertAlias(String alias) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false",
					"sibgha", "1234asdf");

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

	private static double calculateValue(String unit, Double value) {
		
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
		return value;
	} 
}

class Report {
	private Integer companyId;
	private Integer reportId;
	private String reportUri;
	private String companyName;

	public Report() {
	}

	public Report(Integer companyId, Integer reportId, String reportUri, String companyName) {
		this.companyId = companyId;
		this.reportId = reportId;
		this.reportUri = reportUri;
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportUri() {
		return reportUri;
	}

	public void setReportUri(String reportUri) {
		this.reportUri = reportUri;
	}
}