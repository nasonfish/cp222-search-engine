
public class QueryLocation implements Comparable<QueryLocation> {
	private final String word;
	private final String fileName;
	private final String context;
	
	public QueryLocation(String word, String fileName, String context) {
		this.word = word;
		this.fileName = fileName;
		this.context = context;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getContext() {
		return context;
	}
	
	public String getWord() {
		return word;
	}
	
	public String toString() {
		return String.format("The word %s is found in file %s. %s", this.word, this.fileName, this.context);
	}
	
	public int compareTo(QueryLocation other) {
		return this.getFileName().compareTo(other.getFileName());
	}
}
