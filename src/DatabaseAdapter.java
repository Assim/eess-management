import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class DatabaseAdapter {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/eess";
    static final String USER = "eess";
    static final String PASS = "eess";
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    
    public static final String FIELD_PAID = "paid";
    public static final String FIELD_DELIVERED = "delivered";
    
    private void connect() {
        try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (Exception e) { e.printStackTrace(); }
    }
    
    private void disconnect() {
        try {
			conn.close();
		} catch (SQLException e) { e.printStackTrace(); }
    }
    
    public boolean checkUser(String username, String password) {
    	boolean result = false;
    	connect();
    	
    	try {
    		stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM users WHERE username = '"+username+"' AND password = '"+password+"'");
			if(rs.next()) {
				result = true;
			}
	    	rs.close();
	    	stmt.close();
		} catch (SQLException e) { e.printStackTrace(); }
    	disconnect();
		return result;
    }
    
    public ArrayList<String> getOrders(boolean paid, boolean delivered) {
		ArrayList<String> results = new ArrayList<String>();
		
    	connect();
        
    	try {
    		stmt = conn.createStatement();
    		int paidNum = 0;
    		if(paid)
    			paidNum = 1;
    		int deliveredNum = 0;
    		if(delivered)
    			deliveredNum = 1;
    		
    		String sql = "SELECT * FROM orders WHERE paid = '"+ paidNum +"' AND delivered = '"+ deliveredNum +"' ORDER BY order_id DESC";
    		
    		rs = stmt.executeQuery(sql);
    		
    		while(rs.next()) {
    			String text = rs.getInt("order_id") + " | " + rs.getString("phone") + " | " + convertTimestampToDate(rs.getInt("datetime")) + " | " + rs.getFloat("amount") + " R.O.";
    			results.add(text);
    		}
    		rs.close();
    		stmt.close();
    	}
    	catch (Exception e) {}
    	
        disconnect();
        
    	return results;
    }
    
    public String getOrder(int orderId) {
    	String text = "";
    	connect();
    	
    	try {
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("SELECT * FROM orders WHERE order_id = '"+ orderId +"'");
    		if(rs.next()) {
    			text = rs.getInt("order_id") + " | " + rs.getString("phone") + " | " + convertTimestampToDate(rs.getInt("datetime")) + " | " + rs.getFloat("amount") + " R.O." + " | " + rs.getInt("paid") + " | " + rs.getInt("delivered");
    		}
    		rs.close();
    		stmt.close();
    	} catch (Exception e) {}
    	
    	disconnect();
		return text;
    }
    
    public void exportItemsToFile(int orderId, File file) {
    	connect();
    	
    	try {
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("SELECT items FROM orders WHERE order_id = '"+ orderId +"'");
    		if(rs.next()) {
    			file.createNewFile();
    			FileWriter writer = new FileWriter(file, false);
    			writer.write("{\"items\":");
    			writer.write(rs.getString("items"));
    			writer.write("}");
    			writer.close();
    		}
    	} catch (Exception e) {}
    	
    	disconnect();
    }
    
    public void updateBooleanField(int orderId, String field, boolean value) {
    	connect();
    	
    	int num = 0;
    	if(value)
    		num = 1;
    	
    	try {
    		stmt = conn.createStatement();
    		stmt.executeUpdate("UPDATE orders SET "+ field +" = '"+ num +"' WHERE order_id = '"+ orderId +"'");
    	} catch (Exception e) {}
    	
    	disconnect();
    }
    
    public ArrayList<String> getOrdersByPhone(String phone) {
		ArrayList<String> results = new ArrayList<String>();
    	connect();
    	
    	try {
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("SELECT * FROM orders WHERE phone = '"+ phone +"' ORDER BY order_id DESC");
    		while(rs.next()) {
    			results.add(rs.getInt("order_id") + " | " + rs.getString("phone") + " | " + convertTimestampToDate(rs.getInt("datetime")) + " | " + rs.getFloat("amount") + " R.O." + " | Paid: " + rs.getInt("paid") + " | Delivered: " + rs.getInt("delivered"));
    		}
    		rs.close();
    		stmt.close();
    	} catch (Exception e) {}
    	
    	disconnect();
		return results;
    }
    
    public boolean updateUserPassword(String username, String oldPassword, String newPassword) {
    	boolean result = false;
    	
    	connect();
    	
    	try {
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("SELECT * FROM users WHERE username = '"+username+"' AND password = '"+oldPassword+"'");
    		if(rs.next()) {
    			result = true;
    		}
    		rs.close();
    		
    		if(result)
    			stmt.executeUpdate("UPDATE users SET password = '"+newPassword+"' WHERE username = '"+username+"'");
    		stmt.close();
    	} catch (Exception e) {}
    	
    	disconnect();
    	
    	return result;
    }
    
	private String convertTimestampToDate(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timestamp * 1000);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String dateString = sdf.format(calendar.getTime());
		
		return dateString;
	}
}