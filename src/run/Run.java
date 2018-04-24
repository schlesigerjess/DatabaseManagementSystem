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


	// TODO: Insert data into the tables.

	public static void main(String args[])  throws Exception {
		Run JDBC = new Run();
		JDBC.activateJDBC();
		JDBC.getConnection();

		// Optional, comment if you don't want to recreate DB
		JDBC.resetDatabase();

		// JDBC.createRandomData(500000);

		// Does 100 select statements 20 times alternating between columns.
		//		for (int j = 0; j < 20; j++) 
		//		{
		//			String column = "";
		//			if (j % 2 == 0) 
		//			{
		//				column = "FirstInt";
		//			} else
		//				column = "SecondInt";
		//			String startTimeStamp = new SimpleDateFormat("mm:ss.sss").format(new Date());
		//			for (int i = 0; i < 100; i++) 
		//			{
		//				JDBC.selectStatements(column);
		//			}
		//			String endTimeStamp = new SimpleDateFormat("mm:ss.sss").format(new Date());
		//			System.out.println("Start:\t" + startTimeStamp + "\nEnd:\t" + endTimeStamp);
		//		}


		// Gets current tables
		ArrayList<String> tables = JDBC.getTables();
//		for (int i=0;i<tables.size();i++) {
//			System.out.println(tables.get(i));
//		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Select_Table(tables.toArray(new String[0]));
			}
		});
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
			Statement stmt = m_dbConn.createStatement();
			stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
			stmt = m_dbConn.createStatement();
			stmt.execute("DROP TABLE IF EXISTS "+tables.get(i)+" CASCADE");
			stmt = m_dbConn.createStatement();
			stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
		}
		initializeDatabase();
	}

	/**
	 * Reads in our text file for creating tables. 
	 * Then creates the tables and initializes with data
	 * @author Jessica Schlesiger
	 */
	private void initializeDatabase() {
		String SQLTables = readFile("Create_SQL_Tables.txt");
		String[] createTables = SQLTables.split(";");
		for (int i=0;i<createTables.length;i++) {
//			System.out.println(createTables[i]);
			createTable(createTables[i]);

		}
	}
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
	 * Creates a SQL table using the given string
	 * 
	 * @param tableString
	 *            the SQL command to create the table
	 * @author Jessica Schlesiger
	 */
	public void createTable(String tableString) 
	{
		try {
			Statement stmt = m_dbConn.createStatement();
			stmt = m_dbConn.createStatement();
			stmt.execute(tableString);
		} catch (SQLException e) 
		{
			System.out.println("An error has occured on Table Creation.");
			e.printStackTrace();
		}
	}

	/**
	 * Fills our table with random data
	 * 
	 * @param rows
	 *            number of rows we want to create
	 * @throws SQLException
	 * @author Jessica Schlesiger
	 */
	public void createRandomData(int rows) throws SQLException 
	{
		PreparedStatement stmt = m_dbConn.prepareStatement("INSERT INTO Test_Schlesiger "
				+ "(FirstInt, SecondInt, FirstString, SecondString, Doubs) " + "VALUES(?,?,?,?,?)");

		String startTimeStamp = new SimpleDateFormat("mm:ss").format(new Date());

		// Adds each value to the row.
		for (int i = 0; i < rows; i++) 
		{
			stmt.setInt(1, i + 1);
			stmt.setInt(2, i + 1);
			stmt.setString(3, generateRandomString(10));
			stmt.setString(4, generateRandomString(generateRandomNumber(0, 30)));
			stmt.setDouble(5, generateRandomDouble(0, 200));
			// Add row to the batch.
			stmt.addBatch();
		}
		try {
			// Batch is ready, execute it to insert the data
			stmt.executeBatch();
		} catch (SQLException e) 
		{
			System.out.println("Error message: " + e.getMessage());
		}

		String endTimeStamp = new SimpleDateFormat("mm:ss").format(new Date());
		System.out.println("Start:\t" + startTimeStamp + "\nEnd:\t" + endTimeStamp);
	}

	/**
	 * To execute an SQL statement that is a SELECT statement.
	 * @param column the column we want to select from.
	 * @throws SQLException
	 * @author Jessica Schlesiger
	 */
	public void selectStatements(String column) throws SQLException 
	{
		String selectData = new String("SELECT * FROM Test_Schlesiger WHERE " + column + "='1';");
		Statement stmt = m_dbConn.createStatement();
		stmt.executeQuery(selectData);
	}

	/**
	 * Generates a random number between and including min and max
	 * 
	 * @param min
	 *            the number can be
	 * @param max
	 *            the number can be
	 * @return generated number
	 * @author Jessica Schlesiger
	 */
	public int generateRandomNumber(int min, int max) 
	{
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * Generates a random double between and including min and max
	 * 
	 * @param min
	 *            the number can be
	 * @param max
	 *            the number can be
	 * @return generated number
	 * @author Jessica Schlesiger
	 */
	public double generateRandomDouble(double min, double max) 
	{
		Random r = new Random();
		double randomValue = min + (max - min) * r.nextDouble();
		return randomValue;
	}

	/**
	 * Generates a random String of length between and including min and max
	 * 
	 * @param min
	 *            the length can be
	 * @param max
	 *            the length can be
	 * @return generated number
	 * @author Jessica Schlesiger
	 */
	public String generateRandomString(int max) 
	{
		final String AB = "abcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(max);
		for (int i = 0; i < max; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
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



