
/*******************************************************************************
 * Copyright (c) 2021 John Knapp
 * All rights reserved. 
 *******************************************************************************/

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import recipemd.MarkdownConverter;
import recipemd.RecipeMarkupLexer;
import recipemd.RecipeMarkupParser;
import recipemd.RecipeMarkupParser.RecipeContext;

public class FooTest {

	public void testSampleInputs() throws IOException {
		
		FileInputStream fis = new FileInputStream("./src/test/resources/chile_verde.md");

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
		Writer writer = new PrintWriter(System.out);
		
		MarkdownConverter converter = new MarkdownConverter(writer);
		ParseTreeWalker.DEFAULT.walk(converter, recipe);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) {
		FooTest foo = new FooTest();
		try {
			foo.testSampleInputs();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
