/**
 * QueryLocation object-- for storing what word we found, in what file, and the context surrounding
 * the word that we found.
 * 
 * @author Daniel Barnes '21 and Elise Glaser '20
 *
 */
public class QueryLocation implements Comparable<QueryLocation> {
	/**
	 * String word.
	 */
	private final String word;
	/**
	 * String filename.
	 */
	private final String fileName;
	/**
	 * Roughly seven words surrounding the word we found, to output a readable result.
	 */
	private final String context;
	private int count;

	/**
	 * Initialize fields during instantiation.
	 */
	public QueryLocation(String word, String fileName, String context, int count) {
	
		this.word = word;
		this.fileName = fileName;
		this.context = context;
		this.count = count;
	}

	/**
	 * Get the file name we're working with.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	public int getCount(){
		return count;
	}

	public void addCount(){
		count++;
	}

	public String toString(String[] queries) {
		String querystring = String.join(" and ", queries);
		return String.format("%s can be found in file %s. %s", querystring, this.fileName, this.context);
	}

	/**
	 * Get the seven word context surrounding the word, for result printing.
	 * @return String
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * Get the word that this is a location for.
	 * @return String
	 */
	public String getWord() {
		return word;
	}
	
	/**
	 * Convert to a readable string, to be printed when this location was found by a user's query.
	 */
	public String toString() {
		return String.format("The word %s is found in file %s. %s", this.word, this.fileName, this.context);
	}
	
	/**
	 * We are comparable, in order to allow the comparison of locations
	 * during multi-word queries.
	 */
	public int compareTo(QueryLocation other) {
		if (this.getCount()>other.getCount()){
			return -1;
		}else if (this.getCount()<other.getCount()){
			return 1;
		}else{
			return 0;
		}
	}
}
