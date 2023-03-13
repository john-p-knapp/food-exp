package recipemd.dynalist.request;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DocumentMod {
	String token;
	String file_id;
	List<Change> changes;

	public String getJSON() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n");
		buffer.append("\"token\":\"");
		buffer.append(token);
		buffer.append("\", \n");
		buffer.append("\"file_id\":\"");
		buffer.append(file_id);
		buffer.append("\", \n");
		buffer.append("\"changes\": ");
		buffer.append(changes.stream().map(c -> c.getJSON()).collect(Collectors.joining(",", "[", "]")));
		buffer.append("\n}");
		return buffer.toString();
	}
}