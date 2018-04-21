package run;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
	
	
	public static final String DB_LOCATION = "jdbc:mysql://db.cs.ship.edu:3306/csc371-13";
	public static final String LOGIN_NAME = "csc371-08";
	public static final String PASSWORD = "Password08";
	protected Connection m_dbConn = null;


	// TODO: Create the tables and insert data.
	
	
	public static void main(String args[])  throws Exception {
//		Run JDBC = new Run();
//		JDBC.activateJDBC();
//		JDBC.getConnection();

		// String createTable =
		// "CREATE TABLE Test_Schlesiger("
		// + "FirstInt INT,"
		// + "SecondInt INT,"
		// + "FirstString CHAR(10),"
		// + "SecondString VARCHAR(30),"
		// + "Doubs DOUBLE(40, 2),"
		// + "PRIMARY KEY(FirstInt));";
		//
		// JDBC.createTable(createTable);
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
		
		//String[] tables = JDBC.getTables();
		String[] tables = {"Table 1", "Table 2", "Table 3", 
				"Table 4", "Table 5", "Table 6", "Table 7", "Table 8", "Table 9", 
				"Table 10", "Table 11", "Table 12"};
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Select_Table(tables);
			}
		});
	}
	
	
	// TODO Code needs to be tested; I'm not on campus to try it out.
	/**
	 * Grabs all the SQL's tables and creates a string array of them.
	 * @return array of all the tables
	 * @throws Exception failed to get the tables
	 */
	private String[] getTables() throws Exception{
	
		DatabaseMetaData md = m_dbConn.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
		  System.out.println(rs.getString(3));
		}
		return null;
	}



	/**
	 * Creates a SQL table using the given string
	 * 
	 * @param tableString
	 *            the SQL command to create the table
	 */
	public void createTable(String tableString) 
	{
		try {
			Statement stmt = m_dbConn.createStatement();
			stmt.execute("DROP TABLE IF EXISTS Test_Schlesiger CASCADE");
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

	

