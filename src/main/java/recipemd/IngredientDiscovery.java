package recipemd;

import java.io.BufferedReader;
import java.io.File;

/*******************************************************************************
 * Copyright (c) 2013, 2017 John Knapp
 * All rights reserved. 
 *******************************************************************************/

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import recipemd.RecipeMarkupParser.AdditionContext;
import recipemd.RecipeMarkupParser.MeasuredAdditionContext;
import recipemd.RecipeMarkupParser.RecipeContext;

public class IngredientDiscovery {

	public class IngredientListener extends RecipeMarkupBaseListener {

		Set<String> ingredients = new HashSet<>();

		@Override
		public void enterAddition(AdditionContext ctx) {
			ingredients.add(getText(ctx.ingredient).toLowerCase());
		}

		@Override
		public void enterMeasuredAddition(MeasuredAdditionContext ctx) {
			ingredients.add(getText(ctx.ingredient).toLowerCase());
		}

		private String getText(List<Token> tokens) {
			Token t;
			StringBuilder sb = new StringBuilder();
			int size = tokens.size();
			for (int i = 0; i < size; i++) {
				t = tokens.get(i);
				sb.append(t.getText());
				if (i < size - 1) {
					sb.append(" ");
				}
			}
			return sb.toString();
		}

	}

	public void processAll() throws IOException {
		File recipeDir = new File("./src/main/resources/recipe_files/");
		IngredientListener listener = new IngredientListener();

		for (final File fileEntry : recipeDir.listFiles()) {
			collectIngredients(fileEntry, listener);
		}

		List<String> sortedList = new ArrayList<String>(listener.ingredients);
		Collections.sort(sortedList);

		Set<String> groceryStoreMapped = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./src/main/resources/grocery_sort.csv"));

			String line = reader.readLine();

			while (line != null) {
				String[] split = line.split(",");
				if (split.length != 4) {
					System.out.println("split error: " + line);
				} else {
					String key = split[0].toLowerCase();
					groceryStoreMapped.add(key);
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		for (String s : sortedList) {
			if (!groceryStoreMapped.contains(s)) {
				System.out.println(s);
			}

		}
	}

	public void collectIngredients(File recipeFile, ParseTreeListener listener) throws IOException {

		FileInputStream fis = new FileInputStream(recipeFile);
		CharStream stream = new UnbufferedCharStream(fis);
		Lexer lexer = new RecipeMarkupLexer(stream);
		lexer.setTokenFactory(new CommonTokenFactory(true));

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		RecipeMarkupParser parser = new RecipeMarkupParser(tokens);
		RecipeContext recipe = parser.recipe();
		ParseTreeWalker.DEFAULT.walk(listener, recipe);
		fis.close();
	}

	public static void main(String[] args) {
		IngredientDiscovery foo = new IngredientDiscovery();
		try {
			foo.processAll();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
