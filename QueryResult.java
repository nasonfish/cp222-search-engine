import java.util.ArrayList;

public class QueryResult {
	private final String word;
	private final ArrayList<QueryLocation> locations;
	
	public QueryResult(String word) {
		this.word = word;
		locations = new ArrayList<QueryLocation>();
	}
	
	public String getWord() {
		return this.word;
	}
	
	public ArrayList<QueryLocation> getLocations(){
		return this.locations;
	}
	
	public void addLocation(String fileName, String context) {
		this.locations.add(new QueryLocation(this.word, fileName, context));
	}
	
}
