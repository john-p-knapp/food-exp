package recipemd.dynalist.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Delete extends Change {

	String node_id;

	public String getJSON() {
		return "{\n\"action\": \"delete\",\n" + "\"node_id\": " + node_id + "\",\n" + "}";
	}
}