package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 * This screen will allow the user to select entries they would like to update
 * or create a new entry
 * 
 * @author Kim O'Neill
 */

public class Entry_List {
	public static final String DB_LOCATION = "jdbc:mysql://db.cs.ship.edu:3306/csc371-08";
	public static final String LOGIN_NAME = "csc371-08";
	public static final String PASSWORD = "Password08";

	public static final int width = 250;
	public static final int height = 200;
	protected Connection m_dbConn = null;

	Entry_List(String table_name) {
		try {
			Connect();
			JFrame f = new JFrame();
			Statement stmt = m_dbConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table_name);
			ResultSetMetaData metadata = rs.getMetaData();
			int colCount = metadata.getColumnCount();
			String[] columns = new String[colCount];
			Object[][] data = new Object[25][colCount];

			for (int i = 1; i <= colCount; i++) {
				columns[i - 1] = metadata.getColumnLabel(i);
			}
			int k = 0, j = 0;
			while (rs.next()) {
				do {
					data[k][j] = rs.getObject(j + 1);
					j++;
				} while (!(j == colCount));
				k++;
			}
			JTable table = new JTable(data, columns);
			f.add(new JScrollPane(table));
			f.setTitle(table_name);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.pack();
			f.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Connect() {
		try {
			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			
		} catch (SQLException sqle) {
			
			sqle.printStackTrace();
		}
		
		try {
			
			m_dbConn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
	}
}