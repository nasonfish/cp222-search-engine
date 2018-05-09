import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

import org.jsoup.nodes.Document;

/**
 * Search Engine -- Group Project assigned on Day 11.
 * 
 * This program takes in a site directory as an argument,
 * and indexes that site for searching. The program reads in the site,
 * puts each word into a HashMap of Query Results (containing a list of QueryLocations),
 * and when that word is searched for, we output our QueryLocations-- where the word
 * was found in the site we indexed.
 * 
 * @author Daniel Barnes '21 and Elise Glaser '20
 *
 */
public class Day11 {
	
	/**
	 * Our main HashMap, which holds a QueryResult object for each
	 * String word we find in the website.
	 */
	private HashMap<String, QueryResult> dataMap;
	
	private final boolean verbose;
	
	public boolean getVerbosity() {
		return verbose;
	}
	
	/**
	 * Main method
	 * @param args: args[0] should be a relative or absolute path to the site directory
	 * we want to index and search.
	 */
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println(String.format("Usage: java -cp jsoup.jar:. Day11 [-v] <site-directory>"));
			System.exit(1);
			return;
		}
		boolean verbose = false;
		File parentDir = null;
		for(String arg : args) {
			if(arg.startsWith("-")) {
				if(arg.equals("-v")) {
					verbose = true;
				} else {
					System.out.println("Misunderstood flag. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
				}
			} else if (parentDir == null){
				parentDir = new File(arg);
			} else {
				System.out.println("Extraneous argument. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
			}
		}
		if(parentDir == null) {
			System.out.println("Directory not provided. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
		}
		Day11 instance = new Day11(verbose);
		instance.loadFiles("", parentDir);
		System.out.println(instance.dataMap.size());
		Scanner s = new Scanner(System.in);
		System.out.print("Enter Query: ");
		while(s.hasNextLine()) {
			String query = s.nextLine();
			// split query
			// for each, get Query Result
			// loop through each query result and compare the amounts on each file.
			QueryResult result = instance.dataMap.get(query);
			//for (QueryLocation a : result.getLocations()) a.getFileName()
			if (result == null) {
				System.out.println("Not found.");
			} else {
				for(QueryLocation location : result.getLocations()) {
					System.out.println(location.toString());
				}
			}
			System.out.print("Enter Query: ");
		}
		s.close();
	}
	
	/**
	 * Instantiate the class-- initialize a new HashMap.
	 */
	public Day11(boolean verbose) {
		this.dataMap = new HashMap<String, QueryResult>();
		this.verbose = verbose;
	}
	
	/**
	 * Load in the files in this directory recursively.
	 * @param parent The string at the beginning of the directory name. "" for the initial call.
	 * @param parentDir A File object for the file we're recursively loading files into our index from.
	 */
	public void loadFiles(String parent, File parentDir) {
		if(!parentDir.exists()) {
			return;
		}
		if(parentDir.isDirectory()) {
			for(String child : parentDir.list()) {
				loadFiles(parent + parentDir.getName() + "/", new File(parentDir.getAbsolutePath() + "/" + child));
			}
			return;
		}
		assert(parentDir.isFile());
		this.loadText(parent + parentDir.getName(), parentDir);
	}
	
	/**
	 * Given the path of the file we're reading from,
	 * and the text content of the file, load each word individually into our HashMap
	 * as QueryLocation objects to be stored in a QueryResult.
	 * @param path String path to the file we're reading from.
	 * @param parentDir File which we grab the text from. We split by the regex [^a-zA-Z]+,
	 * which means we get only words which use the alphabet, and no special characters.
	 */
	public void loadText(String path, File parentDir) {
		HashMap<String, QueryResult> tempData = null;
		File dataLocation = new File("./.data/" + path);
		try {
			tempData = HashMapUtils.read(new File(path), dataLocation, verbose);
		} catch (NoSuchAlgorithmException | IOException e1) {
			System.out.println("Something went wrong trying to read the data in. whatever");
		}
		if(tempData != null) {
			this.dataMap.putAll(tempData);
			return;
		}
		
		tempData = new HashMap<String, QueryResult>();
		
		Document doc = DataUtils.pullDocument(parentDir);
		String content = doc.text();
		String[] words = content.split("[^a-zA-Z]+");
		for(int i = 0; i < words.length; i++) {
			if(words[i] == "") continue;
			String word = words[i];
			String index = word.toLowerCase();
			QueryResult qr = tempData.get(index);
			if(qr == null) {
				qr = new QueryResult(index);
				tempData.put(index, qr);
			}
			qr.addLocation(path, getSurrounding(words, i));
		}
		try {
			HashMapUtils.dump(dataLocation, new File(path), tempData, verbose);
		} catch (NoSuchAlgorithmException | IOException e) {
			System.out.println("could not write cache files.");
			e.printStackTrace();
			System.exit(1);
		}
		this.dataMap.putAll(tempData);
	}
	
	/**
	 * Static method for getting the context around a word. Given an array of words,
	 * we get
	 * @param words
	 * @param i
	 * @return
	 */
	public static String getSurrounding(String[] words, int i) {
		String a = "";
		int context = 7;
		for(int j = i-context; j < i+context; j++) {
			if (j < 0 || j >= words.length) continue;
			a += words[j] + " ";
		}
		return a;
	}
}
