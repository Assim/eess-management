import java.security.MessageDigest;


public class Util {
	public static String getStringMd5(String text) {
		StringBuffer sb = new StringBuffer();
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
	        md.update(text.getBytes());
	 
	        byte byteData[] = md.digest();
	        
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} catch (Exception e) {}
		
        return sb.toString();
	}
}
