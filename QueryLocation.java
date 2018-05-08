
public class QueryLocation implements Comparable<QueryLocation> {
	private final String word;
	private final String fileName;
	private final String context;
	private int count;

	public QueryLocation(String word, String fileName, String context, int count) {
		this.word = word;
		this.fileName = fileName;
		this.context = context;
		this.count = count;
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
