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

public class HashMapUtils {
	public static void dump(File outFile, File inFile, HashMap<String, QueryResult> data, boolean verbose) throws NoSuchAlgorithmException, IOException {
		//outFile.mkdirs();
		if(verbose) System.out.println("Serializing entries to be outputted in file " + outFile.getName());
		outFile.getParentFile().mkdirs();
		outFile.createNewFile();
		PrintStream os = new PrintStream(new FileOutputStream(outFile));
		os.println(md5(inFile));
		for(Entry<String, QueryResult> entry : data.entrySet()) {
			os.println(serialize(entry));
		}
		os.close();
	}
	
	public static String serialize(Entry<String, QueryResult> entry) {
		QueryResult qr = entry.getValue();
		return String.format("%s\\%s", entry.getKey(), String.join(";", toStrings(qr.getLocations())));
	} // hello\file*context;file*context;file*context
	
	public static String[] toStrings(ArrayList<QueryLocation> locs) {
		String[] strings = new String[locs.size()];
		for(int i = 0; i < locs.size(); i++) {
			strings[i] = locs.get(i).serialize();
		}
		return strings;
	}
	
	// https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	public static String md5(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		DigestInputStream dis = new DigestInputStream(is, md);
		byte[] b = new byte[1024];
		while(dis.read(b) != -1) {}
		byte[] bytes = dis.getMessageDigest().digest();
		String hex = DatatypeConverter.printHexBinary(bytes).toUpperCase();
		dis.close();
		return hex;
	}

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
		while(s.hasNextLine()) {
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
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		File f = new File("Day11.java");
		System.out.println(md5(f));

		f = new File("QueryResult.java");
		System.out.println(md5(f));
	}

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
