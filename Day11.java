import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.jsoup.nodes.Document;

public class Day11 {
	
	private HashMap<String, QueryResult> dataMap;
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println(String.format("Usage: java Day11 <site-directory>"));
			System.exit(1);
			return;
		}
		Day11 instance = new Day11();
		File parentDir = new File(args[0]);
		instance.loadFiles("", parentDir);
		System.out.println(instance.dataMap.keySet());
		Scanner s = new Scanner(System.in);
		System.out.print("Enter Query: ");
		while(s.hasNextLine()) {
			String query = s.nextLine();
			QueryResult result = instance.dataMap.get(query);
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
	
	public Day11() {
		this.dataMap = new HashMap<String, QueryResult>();
	}
	
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
		Document doc = DataUtils.pullDocument(parentDir);
		this.loadText(parent + parentDir.getName(), doc.text());
	}
	
	public void loadText(String path, String content) {
		String[] words = content.split("[^a-zA-Z]+");
		for(int i = 0; i < words.length; i++) {
			if(words[i] == "") continue;
			String word = words[i];
			String index = word.toLowerCase();
			QueryResult qr = dataMap.get(index);
			if(qr == null) {
				qr = new QueryResult(index);
				dataMap.put(index, qr);
			}
			qr.addLocation(path, getSurrounding(words, i)); // TODO make word into context-- we'll have to change our loop.
		}
	}
	
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
