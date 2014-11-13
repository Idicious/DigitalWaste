package helpsers;

import java.sql.*;

public class MySQL {
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/digitalwaste";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "";
	
	private Connection conn;
	
	public MySQL()
	{
		conn = connect();
	}
	
	public ResultSet pull(String query){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			stmt.close();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean push(String query){
		Connection conn = connect();
		Statement stmt;
		int result = -1;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		return false;
	}
	
	private Connection connect(){
		try{
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(DB_URL,USER,PASS);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
