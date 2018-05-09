import java.util.ArrayList;

/**
 * A class to store a location associated with many words.
 * 
 * @author Daniel Barnes '21 Elise Glaser '20
 *
 */
public class WordGroupLocation {
	
	/**
	 * A QueryLocation object, which stores the location and context of one of the words
	 * we found. All of these words in {@link words} can be found in the file {@link loc} refers to.
	 */
	private final QueryLocation loc;
	
	/**
	 * An array containing a list of all words which can be found in {@link loc}'s file.
	 */
	private final ArrayList<String> words;
	
	/**
	 * Instantiate a WordGroupLocation. Set {@link loc} to the QueryLocation,
	 * instantiate the WordList, and add the word which {@link loc} refers to
	 * to the wordlist.
	 * @param loc QueryLocation which we refer to.
	 */
	public WordGroupLocation(QueryLocation loc) {
		this.loc = loc;
		this.words = new ArrayList<String>();
		this.words.add(loc.getWord());
	}

	/**
	 * Get the location this WordGroup refers to.
	 * @return QueryLocation
	 */
	public QueryLocation getLoc() {
		return loc;
	}

	/**
	 * Return an ArrayList of words which can be found in the location we refer to.
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getWords() {
		return words;
	}
	
	/**
	 * Add a word to this WordGroup, provided it doesn't alreaday exist.
	 * @param word String
	 */
	public void addWord(String word) {
		if(!this.words.contains(word))
			this.words.add(word);
	}
	
}
