
public class QueryLocation {
	private final String fileName;
	private final String context;
	
	public QueryLocation(String fileName, String context) {
		this.fileName = fileName;
		this.context = context;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getContext() {
		return context;
	}
}
