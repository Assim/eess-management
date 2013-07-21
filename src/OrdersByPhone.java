import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class OrdersByPhone {

	private JFrame frame;
	private DatabaseAdapter db = new DatabaseAdapter();
	private String phoneNo;

	/**
	 * Create the application.
	 */
	public OrdersByPhone(String phoneNo) {
		this.phoneNo = phoneNo;
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 256);
		frame.getContentPane().setLayout(null);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number: "+phoneNo);
		lblPhoneNumber.setBounds(10, 11, 203, 14);
		frame.getContentPane().add(lblPhoneNumber);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		final JList<String> list = new JList<String>(model);
		list.setBounds(10, 36, 414, 140);
		frame.getContentPane().add(list);
		
		// Populate list
		ArrayList<String> results = db.getOrdersByPhone(phoneNo);
		for (String text : results) {
			model.addElement(text);
		}
		
		JButton btnViewDetails = new JButton("View Details");
		btnViewDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = list.getSelectedValue();
				if(selectedItem != null) {
					String[] split = selectedItem.split("\\|");
					// Remove whitespaces and other non-visible chars
					int orderId = Integer.parseInt(split[0].replaceAll("\\s",""));
						
					new OrderDetails(orderId);

				}
				else {
					JOptionPane.showMessageDialog(frame, "Nothing selected");
				}
			}
		});
		btnViewDetails.setBounds(10, 184, 128, 23);
		frame.getContentPane().add(btnViewDetails);
	}
}