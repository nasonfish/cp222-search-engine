import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

/**
 * Utilities for serializing our HashMap data.
 * 
 * @author Daniel Barnes '21 and Elise Glaser '20
 *
 */
public class HashMapUtils {
	
	/**
	 * Dump data, serialized, into .data file, with an md5 hash.
	 * 
	 * @param outFile File which is ./.data/<filename>, for cache data.
	 * @param inFile File which is <filename>, which we read from.
	 * @param data Data to serialize and print.
	 * @param verbose Verbosity for printing debug data.
	 * @throws NoSuchAlgorithmException If MD5 does not exist in this Java version (I promise it does).
	 * @throws IOException If we are unable to create and write to the outFile.
	 */
	public static void dump(File outFile, File inFile, HashMap<String, QueryResult> data, boolean verbose) throws NoSuchAlgorithmException, IOException {
		if(verbose) System.out.println("Serializing entries to be outputted in file " + outFile.getName());
		outFile.getParentFile().mkdirs();
		outFile.createNewFile(); // Create the data file.
		PrintStream os = new PrintStream(new FileOutputStream(outFile));
		os.println(md5(inFile)); // print an MD5 hash of the file we read from into the data file, for integrity checks.
		for(Entry<String, QueryResult> entry : data.entrySet()) {
			os.println(serialize(entry)); // print lines with serialization.
		}
		os.close(); // close stream to prevent resource leak.
	}
	
	/**
	 * Serialize the an entry in the HashMap into a String to be printed to a file.
	 * @param entry Entry with a String and a QueryResult.
	 * @return String, a serialized form of the HashMap entry.
	 */
	public static String serialize(Entry<String, QueryResult> entry) {
		QueryResult qr = entry.getValue();
		return String.format("%s\\%s", entry.getKey(), String.join(";", toStrings(qr.getLocations())));
	} // hello\file*context;file*context;file*context
	
	/**
	 * Call seriaize on each QueryLocation in an ArrayList.
	 * @param locs ArrayList<QueryLocation>
	 * @return String array of serialized versions of the QueryLocations.
	 */
	public static String[] toStrings(ArrayList<QueryLocation> locs) {
		String[] strings = new String[locs.size()];
		for(int i = 0; i < locs.size(); i++) {
			strings[i] = locs.get(i).serialize();
		}
		return strings;
	}
	/**
	 * Create an MD5 hex checksum of the given file, for integrity testing.
	 * @param file File to hash
	 * @return String hex md5 checksum.
	 * @throws NoSuchAlgorithmException If MD5 does not exist in this version of java (though it is required to.)
	 * @throws IOException If we are unable to read from the file.
	 */
	// https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	public static String md5(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5"); // instance of md5 MessageDigest class.
		InputStream is = new FileInputStream(file); // file input stream
		DigestInputStream dis = new DigestInputStream(is, md);
		byte[] b = new byte[1024];
		while(dis.read(b) != -1) {} // we read through the file into our digest 1024 bytes at a time
		byte[] bytes = dis.getMessageDigest().digest(); // bytes will now be the md5 checksum
		String hex = DatatypeConverter.printHexBinary(bytes).toUpperCase(); // we convert to uppercase hex
		dis.close(); // close to prevent resource leak.
		return hex; // return the hex
	}

	/**
	 * Read in the data from this file into a HashMap. If unsuccessful, return null.
	 * @param file File which originally had the data.
	 * @param dataLocation Data file in ./.data/
	 * @param verbose Verbosity level, for printing
	 * @return HashMap<String, QueryResult> or null.
	 * @throws NoSuchAlgorithmException If MD5 does not exist (it does)
	 * @throws IOException If we cannot read from the files we want to read from.
	 */
	public static HashMap<String, QueryResult> read(File file, File dataLocation, boolean verbose) throws NoSuchAlgorithmException, IOException {
		if(!dataLocation.isFile()) {
			return null;
		}
		Scanner s = new Scanner(new FileInputStream(dataLocation));
		String hash = md5(file);
		if(!s.hasNext()) {
			if(verbose) System.out.println("Loading check failed (data file is empty). " + file.getName());
			s.close();
			return null;
		}
		String comphash = s.nextLine();
		if(!comphash.equals(hash)) {
			if(verbose) System.out.println("Hashing check failed. " + comphash  +" compared to " + hash + " on file " + file.getName());
			s.close();
			return null;
		}
		if(verbose) System.out.println("Hashing check succeeded. " + comphash  +" is the correct hash of file " + file.getName());
		HashMap<String, QueryResult> data = new HashMap<String, QueryResult>();
		while(s.hasNextLine()) { // parse our serialization into a HashMap.
			String[] line = s.nextLine().split("\\\\");
			String word = line[0];
			String[] locs = line[1].split(";");
			QueryResult res = new QueryResult(word);
			for(String l : locs) {
				String[] d = l.split("\\*");
				res.addLocation(d[0], d[1]);
			}
			data.put(word, res);
		}
		s.close();
		return data;
	}

	/**
	 * Merge the querylocations in these HashMaps-- such that the second hashmap is inserted into the first one.
	 * @param dataMap Map to insert into
	 * @param tempData Map to read from.
	 */
	public static void mergeInto(HashMap<String, QueryResult> dataMap, HashMap<String, QueryResult> tempData) {
		for(Entry<String, QueryResult> entry : tempData.entrySet()) {
			if(dataMap.containsKey(entry.getKey())) {
				dataMap.get(entry.getKey()).merge(entry.getValue());
			} else {
				dataMap.put(entry.getKey(), entry.getValue());
			}
		}
		
	}
}
