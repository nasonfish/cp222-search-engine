/**
 * QueryLocation object-- for storing what word we found, in what file, and the context surrounding
 * the word that we found.
 * 
 * @author Daniel Barnes '21 and Elise Glaser '20
 *
 */
public class QueryLocation {
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

	/**
	 * Initialize fields during instantiation.
	 */
	public QueryLocation(String word, String fileName, String context) {
	
		this.word = word;
		this.fileName = fileName;
		this.context = context;
	}

	/**
	 * Get the file name we're working with.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
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
	 * Return a serialization of this QueryLocation, to be printed into a file.
	 * Our format is <fileName>*<context>
	 * @return String serialization.
	 */
	public String serialize() {
		return String.format("%s*%s", this.fileName, this.context);
	}
}
