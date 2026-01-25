package recipemd.dynalist.request;

public class DocumentRequest {

	private String token;
	private String file_id;

	public DocumentRequest(String token, String file_id) {
		super();
		this.token = token;
		this.file_id = file_id;
	}

	public String getJSON() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n \"token\": \"");
		buffer.append(token);
		buffer.append("\",\n \"file_id\": \"");
		buffer.append(file_id);
		buffer.append("\"\n}");

		return buffer.toString();
	}
}