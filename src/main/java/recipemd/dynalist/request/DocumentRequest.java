package recipemd.dynalist.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DocumentRequest {
	
	private String token;
	private String file_id;

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