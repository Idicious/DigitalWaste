package helpers;

import java.sql.*;

import controller.Controller;

public class MySQL {
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/digitalwaste";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "";
	
	private static Controller out = Controller.getInstance();
	
	private Connection conn;
	
	public MySQL()
	{
		this.conn = this.connect();
	}
	
	/**
	 * Used to perform SELECT queries on the database.
	 * @param query
	 * @return
	 */
	public ResultSet pull(String query){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			stmt.close();
			return rs;
		} catch (SQLException e) {
			out.outputLine(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Used to perform INSERT, UPDATE and DELETE queries on the database
	 * @param query
	 * @return
	 */
	public boolean push(String query) {
		Statement stmt;
		int result = -1;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException e) {
			out.outputLine(e.getMessage());
		}
		if(result == 1){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean disconnect() {
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			out.outputLine(e.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Opens and returns a connection to the database
	 * @return
	 */
	private Connection connect() {
		try{
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(DB_URL,USER,PASS);
		}catch(SQLException se){
			out.outputLine(se.getMessage());
		}catch(Exception e){
			out.outputLine(e.getMessage());
		}
		return null;
	}
}
