import java.io.File;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

/**
 * Utility class which provides generic functions which may be re-used. All methods included are static.
 * This is so I don't have to implement reading information from the Internet every single day.
 * 
 * @author Daniel Barnes '21
 */
public class DataUtils {
	
	/**
	 * Create a Jsoup Document file from a java File object.
	 * @param file File object
	 * @return Jsoup Document to be parsed.
	 */
	public static Document pullDocument(File file) {
		try {
			return Jsoup.parse(file, null);
		} catch (IOException e) {
			System.out.println("An error occured trying to read the soup.");
			System.exit(1);
			return null;
		}
	}
}
