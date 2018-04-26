package run;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.SwingUtilities;

import ui.Select_Table;
/**
 * Runs the DBMS UI.
 * 
 * @author Jessica Schlesiger, Jonathan, Kim
 *
 */
public class Run
{
	public static final String DB_LOCATION = "jdbc:mysql://db.cs.ship.edu:3306/csc371-08";
	public static final String LOGIN_NAME = "csc371-08";
	public static final String PASSWORD = "Password08";
	protected Connection m_dbConn = null;


	// TODO: Insert data into the tables needs bugfixes for foreign key constraints
	// TODO: Create a select statement method. In progress one is commented out below 

	public static void main(String args[])  throws Exception {
		Run JDBC = new Run();
		JDBC.activateJDBC();
		JDBC.getConnection();

		// Optional, comment if you don't want to recreate DB
		JDBC.resetDatabase();

		// Gets current tables
		ArrayList<String> tables = JDBC.getTables();
		//		for (int i=0;i<tables.size();i++) {
		//			System.out.println(tables.get(i));
		//		}


		new Select_Table(tables.toArray(new String[0]));
	}

	/**
	 * Resets our database. 
	 * Deletes all current tables and creates new ones based off our text file.
	 * Will Initialize one row of data for each table
	 * @throws Exception if it fails to find the tables or delete the tables
	 * @author Jessica Schlesiger
	 */
	private void resetDatabase() throws Exception {
		ArrayList<String> tables = getTables();
		for (int i=0;i<tables.size();i++) {
			runSQLCommands("SET FOREIGN_KEY_CHECKS=0;");
			runSQLCommands("DROP TABLE IF EXISTS "+tables.get(i)+" CASCADE");
			runSQLCommands("SET FOREIGN_KEY_CHECKS=1;");
		}
		initializeDatabase();
	}

	/**
	 * Reads in our text file for creating tables. 
	 * Then creates the tables and initializes with data
	 * @author Jessica Schlesiger
	 * @throws Exception 
	 */
	private void initializeDatabase() throws Exception {
		String[] createTables = readFile("Create_SQL_Tables.txt").split(";");
		for (int i=0;i<createTables.length;i++) {
			runSQLCommands(createTables[i]);
		}

		// TODO: Diagnose SQL foreign key errors
		String[] insertData = readFile("Insert_Data_SQL.txt").split(";");
		for (int i=0;i<insertData.length;i++) {
			runSQLCommands(insertData[i]);
		}	
	}

	//	/**
	//	 * To execute an SQL statement that is a SELECT statement.
	//	 * @param column the column we want to select from.
	//	 * @author Jessica Schlesiger
	//	 * @throws Exception 
	//	 */
	//	public void selectStatements(String selectString) throws Exception 
	//	{
	
	//		// Was attempting to create a way to select all table's columns and their valid datatypes
	//		// Feel free to delete some if not all code here
	//		Statement stmt = m_dbConn.createStatement();
	//		ResultSet rs = stmt.executeQuery(selectString);
	//		ResultSetMetaData md = rs.getMetaData();
	//		int colCount = md.getColumnCount();  
	//		int i=3;
	//		while (rs.next()) {
	//			System.out.println(rs.getString(i));
	//			
	//		}
	////		for (int i=0; i<=2; i++){  
	////		//String col_name = md.getColumnName(i);  
	////		System.out.println(rs.getString(i));  
	////		}
	//}
	//	



	/**
	 * Reads in a full text file and returns a string containing its contents
	 * @param path to the desired file
	 * @return string of path file's contents
	 * @throws IOException if it fails to read in the file.
	 * @author Jessica Schlesiger
	 */
	static String readFile(String path) {
		try {

			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded);
		}	 catch (IOException e) {
			System.out.println("Failed to read: "+path);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Grabs all the SQL's tables and creates a string array of them.
	 * @return array of all the tables
	 * @throws Exception failed to get the tables
	 * @author Jessica Schlesiger
	 */
	private ArrayList<String> getTables() throws Exception{

		DatabaseMetaData md = m_dbConn.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		ArrayList<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString(3));
		}
		return tables;
	}	

	/**
	 * Executes a SQL command using the given string
	 * Does not work for select statements
	 * @param command the SQL command
	 * @author Jessica Schlesiger
	 */
	public void runSQLCommands(String command) 
	{
		try {
			Statement stmt = m_dbConn.createStatement();
			stmt = m_dbConn.createStatement();
			stmt.execute(command);
		} catch (SQLException e) 
		{
			System.out.println("An error has occured when running: "+command);
			e.printStackTrace();
		}
	}

	/**
	 * This is the recommended way to activate the JDBC drivers, but is only setup
	 * to work with one specific driver. Setup to work with a MySQL JDBC driver.
	 *
	 * If the JDBC Jar file is not in your build path this will not work. I have the
	 * Jar file posted in D2L.
	 *
	 * @return Returns true if it successfully sets up the driver.
	 * @author Given to us for first project
	 */
	public boolean activateJDBC() 
	{
		try 
		{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}

		return true;
	}

	/**
	 * Creates a connection to the database that you can then send commands to.
	 * @author Given to us for first project
	 */
	public void getConnection() 
	{
		try 
		{
			m_dbConn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD);
		} catch (Exception e) 
		{
			System.out.println("Could not connect to DB");
		}
	}
}