import java.util.ArrayList;

public class WordGroupLocation implements Comparable<WordGroupLocation> {
	private final QueryLocation loc;
	private final ArrayList<String> words;
	
	
	public WordGroupLocation(QueryLocation loc) {
		this.loc = loc;
		this.words = new ArrayList<String>();
		this.words.add(loc.getWord());
	}


	public QueryLocation getLoc() {
		return loc;
	}

	public ArrayList<String> getWords() {
		return words;
	}
	
	public void addWord(String word) {
		if(!this.words.contains(word))
			this.words.add(word);
	}

	@Override
	public int compareTo(WordGroupLocation o) {
		return this.words.size() - o.getWords().size();
	}
	
}
