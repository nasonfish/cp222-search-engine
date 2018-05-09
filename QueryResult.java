import java.util.ArrayList;

/**
 * A query result object, which contains a list of QueryLocation objects.
 * 
 * When the user searches for a word, there is one QueryResult object for that
 * word. It is found in the HashMap in Day11.
 * 
 * The QueryResult object contains a set of QueryLocation objects, which can be
 * printed one-by-one as the result set.
 * 
 * @author Daniel Barnes '21 and Elise Glaser '20
 *
 */
public class QueryResult {
	/**
	 * The word this result object refers to.
	 */
	private final String word;
	/**
	 * The QueryLocation objects which describe where to find the aforementioned word.
	 */
	private final ArrayList<QueryLocation> locations;
	
	/**
	 * Instantiate a QueryResult, and initialize its internal ArrayList.
	 * @param word The word to be searched for to retrieve this QueryResult.
	 */
	public QueryResult(String word) {
		this.word = word;
		locations = new ArrayList<QueryLocation>();
	}



	
	/**
	 * Get the word this result refers to.
	 * @return String
	 */
	public String getWord() {
		return this.word;
	}
	
	/**
	 * Get the list of QueryLocation objects.
	 * @returns ArrayList<QueryLocation>
	 */
	public ArrayList<QueryLocation> getLocations(){
		return this.locations;
	}
	
	/**
	 * Add another QueryLocation object to our internal ArrayList.
	 * @param fileName The file the word was found in.
	 * @param context Roughly seven words of context surrounding the word we found.
	 */
	public void addLocation(String fileName, String context) {
		this.locations.add(new QueryLocation(this.word, fileName, context, 0));
	}
}
