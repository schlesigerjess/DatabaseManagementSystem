package ui;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This will allow the user to click a table to interact with in a SQL database.
 * @author Jessica Schlesiger
 */
public class Select_Table {

	private JList<String> tableList;
	public static final int width = 250;
	public static final int height = 200;

	/**
	 * Creates the UI for the given tables
	 * @param tables we want to select from
	 */
	public Select_Table(String[] tables) {
		JFrame frame = new JFrame();
		tableList = new JList<String>(tables);
		tableList.setLayoutOrientation(JList.VERTICAL);

		tableList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				itemSelected(event);
			}
		});
		
		JScrollPane listScroller = new JScrollPane(tableList);
		listScroller.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(listScroller);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * On clicking a table, it will open up a UI to view/modify/delete a table.
	 * @param event The item that was clicked
	 */
	private void itemSelected(javax.swing.event.ListSelectionEvent event) {

		/** Here is where you call the other UI view. 
		* This code can be replaced with however you decide to generate that view
		* It prints out twice because ONCLICK is one action and OFFCLICK is another action. 
		* See what I mean by clicking and holding on a table name, then releasing the click. 
		* It prints out once for each action.
		*/
		System.out.println(tableList.getSelectedValue());
//		String s = (String) tableList.getSelectedValue();
//		if (s.equals("TABLE NAME")) {
//		}
	}

}