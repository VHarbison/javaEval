package fr.epsi.book.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PersistenceManager {
	
	private static final String DB_URL = "jdbc:mysql://192.168.10.50:3306/testDB ";
	private static final String DB_LOGIN = "system";
	private static final String DB_PWD = "P@ssword1";
	
	private static Connection connection;
	
	private PersistenceManager() {}
	
	public static Connection getConnection() throws SQLException {
		if ( null == connection || connection.isClosed() ) {
			connection = DriverManager.getConnection( DB_URL, DB_LOGIN, DB_PWD );
		}
		
		return connection;
	}
	
	public static void closeConnection() throws SQLException {
		if ( null != connection && !connection.isClosed() ) {
			connection.close();
		}
	}
}
