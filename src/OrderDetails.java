import java.io.File;
import java.io.FileReader;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class OrderDetails {

	private int orderId;
	private JFrame frmOrderDetails;
	private DatabaseAdapter db = new DatabaseAdapter();
	private JTextPane textPane;
	private JLabel lblPhoneNumber;
	private JLabel lblDate;
	private JLabel lblAmount;

	/**
	 * Create the application.
	 */
	public OrderDetails(final int orderId) {
		this.orderId = orderId;
		initialize();
		frmOrderDetails.setVisible(true);
		
		String details = db.getOrder(orderId);
		final String[] split = details.split("\\|");
		
		db.exportItemsToFile(orderId, new File("Items.json"));
		
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("Items.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray elems = (JSONArray) jsonObject.get("items");
			for(int i = 0; i < elems.size(); i++) {
				 JSONObject childJSONObject = (JSONObject) elems.get(i);
				 String itemName = (String) childJSONObject.get("name");
				 String unitPrice = (String) childJSONObject.get("unit_price");
				 long qty = (long) childJSONObject.get("qty");
				 float totalPrice = Float.parseFloat(unitPrice) * qty;
				 
				 appendPane(itemName+"\n");
				 appendPane("Qty: "+qty+" - Unit Price: "+unitPrice+" R.O. - Price: "+String.format("%.3f", totalPrice)+" R.O.\n\n");
			}
		} catch(Exception e) {}
		
		textPane.setCaretPosition(0);
		
		lblPhoneNumber = new JLabel("Phone Number: "+split[1]);
		lblPhoneNumber.setBounds(10, 36, 414, 14);
		frmOrderDetails.getContentPane().add(lblPhoneNumber);
		
		lblDate = new JLabel("Date: "+split[2]);
		lblDate.setBounds(10, 61, 414, 14);
		frmOrderDetails.getContentPane().add(lblDate);
		
		lblAmount = new JLabel("Amount: "+split[3]);
		lblAmount.setBounds(10, 86, 414, 14);
		frmOrderDetails.getContentPane().add(lblAmount);
		
		final JCheckBox chckbxPaid = new JCheckBox("Paid");
		chckbxPaid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				db.updateBooleanField(orderId, DatabaseAdapter.FIELD_PAID, chckbxPaid.isSelected());
				JOptionPane.showMessageDialog(frmOrderDetails, "Paid status updated");
			}
		});
		chckbxPaid.setEnabled(false);
		chckbxPaid.setBounds(6, 107, 97, 23);
		frmOrderDetails.getContentPane().add(chckbxPaid);
		
		final JCheckBox chckbxDelivered = new JCheckBox("Delivered");
		chckbxDelivered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				db.updateBooleanField(orderId, DatabaseAdapter.FIELD_DELIVERED, chckbxDelivered.isSelected());
				JOptionPane.showMessageDialog(frmOrderDetails, "Delivered status updated");
			}
		});
		chckbxDelivered.setEnabled(false);
		chckbxDelivered.setBounds(6, 133, 97, 23);
		frmOrderDetails.getContentPane().add(chckbxDelivered);
		
		final JButton btnEditMode = new JButton("Edit Mode");
		btnEditMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frmOrderDetails, "Are you sure you want to enable edit mode? Changes in the checkbox will update the database directly. BE CAREFUL!", null, JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION) {
					btnEditMode.setEnabled(false);
					chckbxPaid.setEnabled(true);
					chckbxDelivered.setEnabled(true);
				}

			}
		});
		btnEditMode.setBounds(335, 127, 89, 23);
		frmOrderDetails.getContentPane().add(btnEditMode);
		
		JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		});
		btnPrint.setBounds(335, 93, 89, 23);
		frmOrderDetails.getContentPane().add(btnPrint);
		
		// Init checkboxes
		if(split[4].replaceAll("\\s","").equals("1")) {
			chckbxPaid.setSelected(true);
		}
		
		if(split[5].replaceAll("\\s","").equals("1")) {
			chckbxDelivered.setSelected(true);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOrderDetails = new JFrame();
		frmOrderDetails.setTitle("Order Details");
		frmOrderDetails.setBounds(100, 100, 450, 433);
		frmOrderDetails.getContentPane().setLayout(null);
		
		JLabel lblOrderId = new JLabel("Order ID: "+this.orderId);
		lblOrderId.setBounds(10, 11, 414, 14);
		frmOrderDetails.getContentPane().add(lblOrderId);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(10, 161, 414, 217);
		//frmOrderDetails.getContentPane().add(textPane);
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setBounds(10, 161, 414, 217);
		frmOrderDetails.getContentPane().add(scrollPane);
	}
	
	public void appendPane(String s) {
		   try {
		      Document doc = textPane.getDocument();
		      doc.insertString(doc.getLength(), s, null);
		   } catch(BadLocationException exc) {
		      exc.printStackTrace();
		   }
	}
}
