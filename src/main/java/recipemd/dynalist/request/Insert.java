package recipemd.dynalist.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Insert extends Change {
	String parent_id;
	String content;
	String note;

	public String getJSON() {
		return "\n{\"action\": \"insert\",\n \"parent_id\": \"" + parent_id + "\",\n \"index\": 0,\n"
				+ "\"content\": \"" + content + "\",\n \"checkbox\": true \n }";

	}
}