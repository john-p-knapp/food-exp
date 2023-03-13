package recipemd.dynalist;

/*******************************************************************************
 * Copyright (c) 2013, 2017 John Knapp
 * All rights reserved. 
 *******************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import recipemd.RecipeMarkupLexer;
import recipemd.RecipeMarkupParser;
import recipemd.RecipeMarkupParser.RecipeContext;
import recipemd.dynalist.IngredientCollector.Ingredient;
import recipemd.dynalist.request.Change;
import recipemd.dynalist.request.DocumentMod;
import recipemd.dynalist.request.DocumentRequest;
import recipemd.dynalist.request.Insert;

public class ListGenerator {

	public static void main(String[] args) {

		ListGenerator foo = new ListGenerator(args[0]);
		try {

			System.out.println(foo.doRead(args[1]));
			System.out.println(foo.publishList(args[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final String token;
	private final String READ = "https://dynalist.io/api/v1/doc/read";
	private final String EDIT = "https://dynalist.io/api/v1/doc/edit";
	private final GrocerySort grocerySort = new GrocerySort();

	public ListGenerator(String token) {
		this.token = token;
	}

	public void collect(File recipeFile, IngredientCollector collector) throws IOException {

		FileInputStream fis = new FileInputStream(recipeFile);

		CharStream stream = new ANTLRInputStream(fis);
		Lexer lexer = new RecipeMarkupLexer(stream);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		RecipeMarkupParser parser = new RecipeMarkupParser(tokens);

		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				throw new IllegalArgumentException("failed to parse at line " + line + " due to " + msg);
			}
		});

		RecipeContext recipe = parser.recipe();
		ParseTreeWalker.DEFAULT.walk(collector, recipe);

		fis.close();
	}

	public Map<String, List<Ingredient>> collectIngredients() throws IOException {

		IngredientCollector collector = new IngredientCollector();
		List<Ingredient> ingredientList = collector.getIngredientList();
		Map<String, List<Ingredient>> grouped = new HashMap<>();

		BufferedReader reader = new BufferedReader(new FileReader("./week.txt"));
		String line = reader.readLine();

		while (line != null) {
			collect(new File("./src/main/resources/recipe_files/" + line), collector);
			// read next line
			line = reader.readLine();
		}

		reader.close();

		for (Ingredient i : ingredientList) {
			List<Ingredient> list = grouped.getOrDefault(i.name, new ArrayList<>());
			list.add(i);
			grouped.put(i.name, list);
		}

		return grouped;
	}

	public String doPost(String url, String params) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
		postConnection.setRequestMethod("POST");
		postConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		postConnection.setRequestProperty("Content-Type", "application/json");
		postConnection.setDoOutput(true);

		OutputStream os = postConnection.getOutputStream();
		os.write(params.getBytes());
		os.flush();
		os.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();

	}

	public String doRead(String file_id) throws Exception {
		DocumentRequest req = new DocumentRequest(token, file_id);
		return doPost(READ, req.getJSON());
	}

	public String publishList(String file_id) throws Exception {

		Map<String, List<Ingredient>> ingredients = collectIngredients();

		List<String> keys = new ArrayList<>(ingredients.keySet());
		keys.sort(grocerySort);

		List<Change> changes = new ArrayList<>();

		Insert insert;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			List<Ingredient> measures = ingredients.get(key);
			String note = measures.stream().map(m -> m.amount + " " + m.unit).collect(Collectors.joining(","));
			insert = new Insert("root", key + " " + grocerySort.getLoc(key) + " (" + note + ")", "");
			changes.add(insert);
		}
		insert = new Insert("root", "Generated Starts Here", "");
		changes.add(insert);

		DocumentMod mod = new DocumentMod(token, file_id, changes);

		return doPost(EDIT, mod.getJSON());
	}

}
