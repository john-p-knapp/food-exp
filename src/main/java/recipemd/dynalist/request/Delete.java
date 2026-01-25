package recipemd.dynalist.request;

public class Delete extends Change {

	private final String node_id;
	
	

	public Delete(String node_id) {
		super();
		this.node_id = node_id;
	}



	public String getJSON() {
		return "{\n\"action\": \"delete\",\n" + "\"node_id\": " + node_id + "\",\n" + "}";
	}
}