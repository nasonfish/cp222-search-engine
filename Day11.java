import java.io.File;
import java.util.HashMap;

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
		instance.loadFiles(parentDir);
		System.out.println(instance.dataMap.keySet());
	}
	
	public Day11() {
		this.dataMap = new HashMap<String, QueryResult>();
	}
	
	public void loadFiles(File parentDir) {
		if(!parentDir.exists()) {
			return;
		}
		if(parentDir.isDirectory()) {
			for(String child : parentDir.list()) {
				loadFiles(new File(parentDir.getAbsolutePath() + "/" + child));
			}
			return;
		}
		assert(parentDir.isFile());
		Document doc = DataUtils.pullDocument(parentDir);
		this.loadText(parentDir.getAbsolutePath(), doc.text());
	}
	
	public void loadText(String path, String content) {
		for(String word : content.split("[^a-zA-Z]")) {
			String index = word.toLowerCase();
			QueryResult qr = dataMap.get(index);
			if(qr == null) {
				qr = new QueryResult(index);
				dataMap.put(index, qr);
			}
			qr.addLocation(path, word); // TODO make word into context-- we'll have to change our loop.
		}
	}
}
