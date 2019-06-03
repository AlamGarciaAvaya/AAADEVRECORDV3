package AAADEVRECORDV3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

public class Encoder {
	/*
	 * Ingresa un archivo al método y lo regresa en Base64 tipo String
	 */
	public static String encoder(String fileName) {
		String base64 = "";
		File file = new File(fileName);
		try (FileInputStream datain = new FileInputStream(file)) {
			byte Data[] = new byte[(int) file.length()];
			datain.read(Data);
//			base64 = Base64.getEncoder().encodeToString(Data);
			base64 = DatatypeConverter.printBase64Binary(Data);
		} catch (FileNotFoundException e) {
			System.out.println("Error" + e);
		} catch (IOException ioe) {
			System.out.println("Ex " + ioe);
		}
		return base64;
	}
	
}