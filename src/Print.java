import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Print {	
	
	public static void print(String type) {
		try {
		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.HELVETICA_BOLD;

		// Logo
		FileInputStream in = new FileInputStream(new File("Receipt Logo.jpg"));
		PDJpeg img = new PDJpeg(document, in);
		
		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		// Draw image
		contentStream.drawImage(img, 0, 700);
		
		// Add multiple lines
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(type+".txt"));
        String line;
        while((line = br.readLine()) != null) {
             lines.add(line);
        }
        br.close();
        
		printMultipleLines(contentStream, font, 8, lines, 0, 700);

		// Make sure that the content stream is closed:
		contentStream.close();

		// Save the results and ensure that the document is properly closed:
		document.save(type+".pdf");
		document.silentPrint();
		document.close();
		}
		catch (Exception e) {}
	}
	
	private static void printMultipleLines(PDPageContentStream contentStream, PDFont font, int fontSize, ArrayList<String> lines, float x, float y) throws IOException {
		if (lines.size() == 0) {
			return;
		}
		final int numberOfLines = lines.size();
		final float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.appendRawCommands(fontHeight + " TL\n");
		contentStream.moveTextPositionByAmount(x, y);
		contentStream.drawString(lines.get(0));
		for (int i = 1; i < numberOfLines; i++) {
			contentStream.appendRawCommands(escapeString(lines.get(i)));
			contentStream.appendRawCommands(" \'\n");
		}
		contentStream.endText();
	}

	private static String escapeString(String text) throws IOException {
		try {
			COSString string = new COSString(text);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			string.writePDF(buffer);
			return new String(buffer.toByteArray(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// every JVM must know ISO-8859-1
			throw new RuntimeException(e);
		}
	}
	
	public static void create(String orderNo, String phoneNo, String datetime, String amount, String pickUpTime) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Receipt_Customer.txt"), false));
			bw.write("Tel: +968 92577735");
			bw.newLine();
			bw.write("CUSTOMER COPY");
			bw.newLine();
			bw.write("USE THIS RECEIPT TO COLLECT YOUR ITEMS");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("Order number: "+orderNo);
			bw.newLine();
			bw.write("Customer phone number: "+phoneNo);
			bw.newLine();
			bw.write("Date and Time: "+datetime);
			bw.newLine();
			bw.write("Total amount due: "+amount);
			bw.newLine();
			bw.write("Pick up time: "+pickUpTime);
			bw.newLine();
			bw.newLine();
			bw.write("____________________________________________________________________________");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("Thank you for using our electronic shopping system");
			bw.newLine();
			bw.write("Come again!");
			bw.newLine();
			bw.write("www.masaaroman.com");
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(new File("Receipt_Store.txt"), false));
			bw.write("Tel: +968 92577735");
			bw.newLine();
			bw.write("STORE COPY");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("Order number: "+orderNo);
			bw.newLine();
			bw.write("Customer phone number: "+phoneNo);
			bw.newLine();
			bw.write("Date and Time: "+datetime);
			bw.newLine();
			bw.write("Total amount due: "+amount);
			bw.newLine();
			bw.write("Pick up time: "+pickUpTime);
			bw.newLine();
			bw.newLine();
			bw.write("____________________________________________________________________________");
			bw.newLine();
			bw.newLine();

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
				 
				 bw.write(itemName);
				 bw.newLine();
				 bw.write("Qty: "+qty+" - Unit Price: "+unitPrice+" R.O. - Price: "+String.format("%.3f", totalPrice)+" R.O.");
				 bw.newLine();
				 bw.newLine();
			}
			
			bw.newLine();
			bw.write("____________________________________________________________________________");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("Thank you for using our electronic shopping system");
			bw.newLine();
			bw.write("Come again!");
			bw.newLine();
			bw.write("www.masaaroman.com");
			bw.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}