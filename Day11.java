import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
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

	private final File parentDir;

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

		// First, we parse the arguments passed into the program at runtime.
		for(String arg : args) {
			if(arg.startsWith("-")) {
				if(arg.equals("-v")) { // verbosity flag (-v)
					verbose = true;
				} else {
					System.out.println("Misunderstood flag. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
					System.exit(1);
				}
			} else if (parentDir == null){
				parentDir = new File(arg);
			} else {
				System.out.println("Extraneous argument. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
				System.exit(1);
			}
		}
		if(parentDir == null) {
			System.out.println("Directory not provided. Usage is: java -cp jsoup.jar:. Day11 [-v] <site-directory>");
			System.exit(1);
		}

		Day11 instance = new Day11(verbose, parentDir);
		instance.go();
	}


	/**
	 * This method is the main engine for our program-- we load in the files from
	 * the website we were passed, and then allow the user to search in our index.
	 */
	public void go() {
		long time = System.currentTimeMillis();
		this.loadFiles("", parentDir);
		long end = System.currentTimeMillis();
		if(verbose) System.out.println(String.format("Done in %d ms", end - time));
		this.userSearch();
	}

	/**
	 * This is a loop which reads in input, searches our index, and returns a list of results.
	 *
	 * We allow for multiword queries here, and we split the search by any character which
	 * is not alphabetical: the regular expression [^a-zA-Z]+.
	 */
	public void userSearch() {
		Scanner s = new Scanner(System.in);

		System.out.print("Enter Query: ");
		queryLoop:
		while(s.hasNextLine()) { // for each new query,
			ArrayList<WordGroupLocation> allLocs = new ArrayList<WordGroupLocation>();
			String query = s.nextLine().toLowerCase(); // read in what the user typed
			String[] queries = query.split("[^a-zA-Z]+"); // split it into words
			for (int i = 0; i < queries.length; i++){ // for each word,
				QueryResult res = this.dataMap.get(queries[i]);
				if(res == null) {
					System.out.println(queries[i] + " was not found.");
					System.out.print("Enter Query: ");
					continue queryLoop; //If there is no result, web pages aren't returned but the user is prompted for a new query
				}
				outerLoop: //Loop to only obtain locations with all words
				for(QueryLocation l : res.getLocations()) {
					for(WordGroupLocation knownLocation : allLocs) {
						if(l.getFileName().equals(knownLocation.getLoc().getFileName())) {
							knownLocation.addWord(l.getWord()); //If the location is already known, it is added to the word group
							continue outerLoop;
						}
					}
					allLocs.add(new WordGroupLocation(l)); //otherwise a new word group is created for the location
				}
			}
			boolean printed = false;
			for(WordGroupLocation knownLocation : allLocs) {
				if(knownLocation.getWords().size() == queries.length) { //if the word group contains all words in the query 
					System.out.println(knownLocation.getLoc().toString(queries));
					printed = true;
				}
			}
			if(!printed) {
				System.out.println("Could not find a file containing all words in the query.");
			}
			System.out.print("Enter Query: ");
		}
		s.close();
	}


	/**
	* Instantiate the class-- initialize a new HashMap.
	*/
	public Day11(boolean verbose, File parentDir) {
		this.dataMap = new HashMap<String, QueryResult>();
		this.parentDir = parentDir;
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
	* @param content String content of the text file. We split by the regex [^a-zA-Z]+,
	* which means we get only words which use the alphabet, and no special characters.
	*/
	public void loadText(String path, File parentDir) {
		HashMap<String, QueryResult> tempData = null;
		File dataLocation = new File("./.data/" + path);
		try {
			tempData = HashMapUtils.read(new File(path), dataLocation, verbose);
		} catch (NoSuchAlgorithmException | IOException e1) {
			System.out.println("Something went wrong trying to read the data in.");
		}
		if(tempData != null) {
			HashMapUtils.mergeInto(this.dataMap, tempData);
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
			System.out.println("Could not write cache files.");
			e.printStackTrace();
			System.exit(1);
		}
		HashMapUtils.mergeInto(this.dataMap, tempData);
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
