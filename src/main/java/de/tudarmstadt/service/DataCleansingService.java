package de.tudarmstadt.service;

import de.tudarmstadt.DbUtil;

import java.sql.*;

/*
 * One: 
        normalize the data by removing the duplications and unit information must be reflected by the indicator value. For example:  
		sales: 13M
		sales: 13M
		
		Normalized form: 
		sales: 13000000
		
 * Two:
		
		remove indicators with numbers and symbols
		
		e.g. 
		25%
		2019 - 2020
		()
 */
public class DataCleansingService {



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
	//TODO handlw the amounts with dollars
	public static void removeGarbageAndMoveIndicators() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					DbUtil.connUrl, DbUtil.user, DbUtil.password);

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select   `indicator_name`, `indicator_id`, `company_id`, `year`, `currency`, `value`, table_number, report_number, context, unit FROM  indicator "
					+ "where  (indicator_name   NOT REGEXP  '^[^A-Za-z]' OR indicator_name    REGEXP   '^– +[A-Za-z]' OR indicator_name REGEXP '^• +[A-Za-z]' OR indicator_name    REGEXP   '^•+[A-Za-z]' )");

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

					String sql = "INSERT IGNORE INTO `mydb`.`normalized_indicator` " +
							"(`indicator_name`, `indicator_id`, `company_id`, `year`, `currency`, `value`, table_number, report_number, context) " +
							"VALUES (?,?,?,?,?,?,?,?,?)";

					// create the mysql insert preparedstatement
					PreparedStatement preparedStmt = con.prepareStatement(sql);
					preparedStmt.setString(1, indicator);
					preparedStmt.setInt(2, indicatorId);
					preparedStmt.setInt(3, companyId);
					preparedStmt.setString(4, year);
					preparedStmt.setString(5, "EUR");
					preparedStmt.setDouble(6, DbUtil.calculateValue(unit, value, currency));
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

	/**
	 * for the purpose of testing the single class
	 * @param args
	 */
	public static void main(String[] args)  {
		removeGarbageAndMoveIndicators();
	}
}
