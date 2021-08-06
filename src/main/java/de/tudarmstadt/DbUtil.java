package de.tudarmstadt;

import java.sql.*;

class DbUtil {
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from company");

			while (rs.next())
				System.out.println(rs.getInt("company_id") + "  " + rs.getString("company_name"));
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertIndicator(Integer companyId ,String indicator, Double value, String currency, String unit, String year, int tableNumber) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");
			// the mysql insert statement
			String query = " insert into indicator (company_id, indicator_name, value, unit, currency, year, table_number)"
					+ " values (?, ?, ?, ?, ?, ?, ?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setInt(1, companyId);
			preparedStmt.setString(2, indicator);
			preparedStmt.setDouble(3, value);
			preparedStmt.setString(4, unit);
			preparedStmt.setString(5, currency);
			preparedStmt.setString(6, year);
			preparedStmt.setInt(7, tableNumber);

			// execute the preparedstatement
			preparedStmt.execute();

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Integer getCompanyId(String companyName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useSSL=false", "sibgha", "1234asdf");

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select company_id from company where company_name = '"+companyName+"'");

			rs.next();
			
			int companyId = rs.getInt("company_id") ;
			
			con.close();
			
			return companyId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}