package recipemd.dynalist.request;

public class Insert extends Change {

	private final String parent_id;
	private final String content;
	private final String note;

	public Insert(String parent_id, String content, String note) {
		super();
		this.parent_id = parent_id;
		this.content = content;
		this.note = note;
	}

	public String getJSON() {
		return "\n{\"action\": \"insert\",\n \"parent_id\": \"" + parent_id + "\",\n \"index\": 0,\n"
				+ "\"content\": \"" + content + "\",\n \"checkbox\": true \n }";

	}
}