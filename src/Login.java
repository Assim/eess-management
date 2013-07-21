import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPasswordField;


public class Login {

	private JFrame frmLogin;
	private JTextField txtUsername;
	private JPasswordField txtPassword;

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
		frmLogin.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogin = new JFrame();
		frmLogin.setTitle("Login");
		frmLogin.setBounds(100, 100, 256, 136);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogin.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(10, 11, 77, 14);
		frmLogin.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(10, 36, 77, 14);
		frmLogin.getContentPane().add(lblPassword);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(84, 8, 140, 20);
		frmLogin.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(84, 33, 140, 20);
		frmLogin.getContentPane().add(txtPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if(txtUsername.getText().equals("")) {
					JOptionPane.showMessageDialog(frmLogin, "Username can't be blank.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(txtPassword.getText().equals("")) {
					JOptionPane.showMessageDialog(frmLogin, "Password can't be blank.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}			
				
				DatabaseAdapter db = new DatabaseAdapter();
				boolean result = db.checkUser(txtUsername.getText(), Util.getStringMd5(txtPassword.getText()));
				
				if(result) {
					new Orders(txtUsername.getText());
					frmLogin.dispose();
				}
				else {
					JOptionPane.showMessageDialog(frmLogin, "Incorrect username/password combination.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnLogin.setBounds(10, 64, 214, 23);
		frmLogin.getContentPane().add(btnLogin);
	}
}
