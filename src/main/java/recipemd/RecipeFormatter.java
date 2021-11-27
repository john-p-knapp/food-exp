package recipemd;

import java.io.File;

/*******************************************************************************
 * Copyright (c) 2013, 2017 John Knapp
 * All rights reserved. 
 *******************************************************************************/

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import recipemd.RecipeMarkupParser.RecipeContext;

public class RecipeFormatter {

	final String outDir = "./recipes/";

	public void convertFiles() throws IOException {
		File recipeDir = new File("./src/main/resources/recipe_files/");
		for (final File fileEntry : recipeDir.listFiles()) {
			convertFile(fileEntry);
		}
	}

	public void convertFile(File recipeFile) throws IOException {

		String outfile = recipeFile.getName();
		outfile = outfile.substring(0, outfile.length() - 4);
		System.out.println(outfile);
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

		Writer writer = new FileWriter(new File(outDir + outfile + ".md"));

		MarkdownConverter converter = new MarkdownConverter(writer);
		ParseTreeWalker.DEFAULT.walk(converter, recipe);
		writer.flush();
		writer.close();
		fis.close();
	}

	public static void main(String[] args) {
		RecipeFormatter foo = new RecipeFormatter();
		try {
			foo.convertFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
