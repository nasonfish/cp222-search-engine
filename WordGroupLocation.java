import java.util.ArrayList;

/**
 * A class to store a location associated with many words.
 * 
 * @author Daniel Barnes '21 Elise Glaser '20
 *
 */
public class WordGroupLocation implements Comparable<WordGroupLocation> {
	
	/**
	 * A QueryLocation object, which stores the location and context of one of the words
	 * we found. All of these words in {@link words} can be found in the file {@link loc} refers to.
	 */
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
