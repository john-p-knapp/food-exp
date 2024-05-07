package recipemd.dynalist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GrocerySort implements Comparator<String> {

	private Map<String, String> dept = new HashMap<>();
	private Map<String, Integer> major = new HashMap<>();
	private Map<String, Integer> minor = new HashMap<>();

	public GrocerySort() {

		try {
			BufferedReader reader = new BufferedReader(new FileReader("./src/main/resources/grocery_sort.csv"));

			String line = reader.readLine();

			while (line != null) {
				String[] split = line.split(",");
				if (split.length != 4) {
					System.out.println("split error: "+ line);
				} else {
					String key = split[0].toLowerCase();
					dept.put(key, split[1]);
					major.put(key, Integer.valueOf(split[2]));
					minor.put(key, Integer.valueOf(split[3]));
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String getLoc(String ingredient) {
		String key = ingredient.toLowerCase();
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		if (dept.get(key) != null) {
			buffer.append(dept.get(key));
			buffer.append(minor.get(key));
		}
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public int compare(String s1, String s2) {
		String o1 = s1.toLowerCase();
		String o2 = s2.toLowerCase();

		Integer i1 = (major.get(o1) == null ? 0 : major.get(o1));
		Integer i2 = (major.get(o2) == null ? 0 : major.get(o2));

		if (i1 == i2) {
			Integer m1 = minor.get(o1);
			Integer m2 = minor.get(o2);
			if (m1 == m2) {
				return o2.compareTo(o1);
			} else {
				return minor.get(o2) - minor.get(o1);
			}
		} else {
			return i2 - i1;
		}

	}
}
