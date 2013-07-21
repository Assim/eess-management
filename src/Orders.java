import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class Orders {

	private JFrame frmEessManagement;
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private JButton btnSetPaid;
	private JButton btnSetDelivered;
	private JCheckBox chckbxPaid;
	private JCheckBox chckbxDelivered;
	private JList<String> list;
	
	private boolean paidSelected;
	private boolean deliveredSelected;
	
	private DatabaseAdapter db = new DatabaseAdapter();
	
	@SuppressWarnings("unused")
	private String username;
	private JButton btnLogout;
	private JButton btnRefresh;

	/**
	 * Create the application.
	 */
	public Orders(String username) {
		this.username = username;
		initialize();
		frmEessManagement.setVisible(true);
		
		refresh();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEessManagement = new JFrame();
		frmEessManagement.setTitle("EESS Management");
		frmEessManagement.setBounds(100, 100, 500, 300);
		frmEessManagement.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEessManagement.getContentPane().setLayout(null);
		
		chckbxPaid = new JCheckBox("Paid");
		chckbxPaid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paidToggle();
			}
		});
		chckbxPaid.setBounds(111, 7, 62, 23);
		frmEessManagement.getContentPane().add(chckbxPaid);
		
		chckbxDelivered = new JCheckBox("Delivered");
		chckbxDelivered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deliveredToggle();
			}
		});
		chckbxDelivered.setBounds(175, 7, 89, 23);
		frmEessManagement.getContentPane().add(chckbxDelivered);
		
		btnSetPaid = new JButton("Set Paid");
		btnSetPaid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changePaidStatus();
			}
		});
		btnSetPaid.setBounds(342, 58, 132, 23);
		frmEessManagement.getContentPane().add(btnSetPaid);
		
		btnSetDelivered = new JButton("Set Delivered");
		btnSetDelivered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeDeliveredStatus();
			}
		});
		btnSetDelivered.setBounds(342, 92, 132, 23);
		frmEessManagement.getContentPane().add(btnSetDelivered);
		
		JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		btnPrint.setBounds(342, 126, 132, 23);
		frmEessManagement.getContentPane().add(btnPrint);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		btnSearch.setBounds(342, 160, 132, 23);
		frmEessManagement.getContentPane().add(btnSearch);
		
		list = new JList<String>(model);
		model.addElement("13433 | 98502800 | 22/22/2222 12:22 | 55.554 R.O.");
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(10, 37, 322, 214);
		frmEessManagement.getContentPane().add(scrollPane);
		
		JLabel lblOrdersSearch = new JLabel("Orders Search:");
		lblOrdersSearch.setBounds(10, 11, 95, 14);
		frmEessManagement.getContentPane().add(lblOrdersSearch);
		
		btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		btnLogout.setBounds(342, 228, 132, 23);
		frmEessManagement.getContentPane().add(btnLogout);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		btnRefresh.setBounds(342, 24, 132, 23);
		frmEessManagement.getContentPane().add(btnRefresh);
		
		JButton btnOrderDetails = new JButton("Order Details");
		btnOrderDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewItems();
			}
		});
		btnOrderDetails.setBounds(342, 194, 132, 23);
		frmEessManagement.getContentPane().add(btnOrderDetails);
	}
	
	private void refresh() {
		ArrayList<String> results = db.getOrders(paidSelected, deliveredSelected);
		model.clear();
		for(String text : results) {
			model.addElement(text);
		}
	}
	
	private void paidToggle() {
		if(chckbxPaid.isSelected()) {
			btnSetPaid.setText("Set Unpaid");
			paidSelected = true;
		} 
		else {
			btnSetPaid.setText("Set Paid");
			paidSelected = false;
		}

		refresh();
	}
	
	private void deliveredToggle() {
		if(chckbxDelivered.isSelected()) {
			btnSetDelivered.setText("Set Undelivered");
			deliveredSelected = true;
		}
		else {
			btnSetDelivered.setText("Set Delivered");
			deliveredSelected = false;
		}
		
		refresh();
	}
	
	private void print() {
		String selectedItem = list.getSelectedValue();
		if(selectedItem != null) {
			final String[] split = selectedItem.split("\\|");
			// Remove whitespaces and other non-visible chars
			final int orderId = Integer.parseInt(split[0].replaceAll("\\s",""));
			
			final PickUpTimeDialog dialog = new PickUpTimeDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.addListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					 String action = e.getActionCommand();
					 if(action.equals("OK")) {
						db.exportItemsToFile(orderId, new File("Items.json"));
						Print.create(split[0], split[1], split[2], split[3], dialog.getText());
						Print.print("Receipt_Customer");
						Print.print("Receipt_Store");		 
					 }
					 else {
						 return;
					 }
					 dialog.dispose();
				}
			});
		}
		else {
			JOptionPane.showMessageDialog(frmEessManagement, "Nothing selected");
		}
	}
	
	private void changePaidStatus() {
		String selectedItem = list.getSelectedValue();
		if(selectedItem != null) {
			int result = JOptionPane.showConfirmDialog(frmEessManagement, "Do you wish to change the paid status?", null, JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				String[] split = selectedItem.split("\\|");
				// Remove whitespaces and other non-visible chars
				int orderId = Integer.parseInt(split[0].replaceAll("\\s",""));
				
				boolean value = true;
				if(paidSelected)
					value = false;
				
				db.updateBooleanField(orderId, DatabaseAdapter.FIELD_PAID, value);
				
				int selectedIndex = list.getSelectedIndex();
				if (selectedIndex != -1) {
				    model.remove(selectedIndex);
				}	
			}
		}
		else {
			JOptionPane.showMessageDialog(frmEessManagement, "Nothing selected");
		}
	}
	
	private void changeDeliveredStatus() {
		String selectedItem = list.getSelectedValue();
		if(selectedItem != null) {
			int result = JOptionPane.showConfirmDialog(frmEessManagement, "Do you wish to change the delivered status?", null, JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				String[] split = selectedItem.split("\\|");
				// Remove whitespaces and other non-visible chars
				int orderId = Integer.parseInt(split[0].replaceAll("\\s",""));
				
				boolean value = true;
				if(deliveredSelected)
					value = false;
				
				db.updateBooleanField(orderId, DatabaseAdapter.FIELD_DELIVERED, value);
				
				int selectedIndex = list.getSelectedIndex();
				if (selectedIndex != -1) {
				    model.remove(selectedIndex);
				}
			}
		}
		else {
			JOptionPane.showMessageDialog(frmEessManagement, "Nothing selected");
		}
	}
	
	private void viewItems() {
		String selectedItem = list.getSelectedValue();
		if(selectedItem != null) {
			String[] split = selectedItem.split("\\|");
			// Remove whitespaces and other non-visible chars
			int orderId = Integer.parseInt(split[0].replaceAll("\\s",""));
				
			new OrderDetails(orderId);

		}
		else {
			JOptionPane.showMessageDialog(frmEessManagement, "Nothing selected");
		}
	}
	
	private void logout() {
		frmEessManagement.dispose();
		new Login();
	}
	
	private void search() {
		final SearchDialog dialog = new SearchDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		dialog.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 String action = e.getActionCommand();
				 if(action.equals("OK")) {
					 // Validation
					 try {
						 Integer.parseInt(dialog.getText());
					 }
					 catch (NumberFormatException nfe) {
						 JOptionPane.showMessageDialog(frmEessManagement, "Has to be numbers only.");
						 return;
					 }
					 
					 if(dialog.getSelectedCriteria().equals("Order ID")) {
						 String result = db.getOrder(Integer.parseInt(dialog.getText()));
						 if(result.length() == 0) {
							JOptionPane.showMessageDialog(frmEessManagement, "Order ID doesn't exist in the system");
							return;
						 }
						 else {
							 new OrderDetails(Integer.parseInt(dialog.getText()));
						 	dialog.dispose();
						 }
					 }
					 else {
						 // Phone number
						 ArrayList<String> result = db.getOrdersByPhone(dialog.getText());
						 if(result.size() == 0) {
							JOptionPane.showMessageDialog(frmEessManagement, "Phone number doesn't exist in the system");
							return;
						 }
						 else {
							 new OrdersByPhone(dialog.getText());
						 	dialog.dispose();
						 }
					 }
				 }
				 else {
					 // Cancel
					 dialog.dispose();
				 }
			}
		});
	}
}