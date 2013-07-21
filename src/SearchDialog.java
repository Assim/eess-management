import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;


public class SearchDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox<String> comboBox;

	/**
	 * Create the dialog.
	 */
	public SearchDialog() {
		setTitle("Search");
		setBounds(100, 100, 170, 138);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(6, 37, 142, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Order ID", "Phone Number"}));
		comboBox.setBounds(6, 6, 142, 20);
		contentPanel.add(comboBox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public String getText() {
		return textField.getText();
	}
	
	public String getSelectedCriteria() {
		return (String) comboBox.getSelectedItem();
	}
	
	public void addListener(ActionListener listener) {
		  okButton.addActionListener(listener);
		  cancelButton.addActionListener(listener);
	}
}