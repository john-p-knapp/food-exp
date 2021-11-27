package recipemd;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import recipemd.RecipeMarkupParser.AdditionContext;
import recipemd.RecipeMarkupParser.CompDefContext;
import recipemd.RecipeMarkupParser.CompRefContext;
import recipemd.RecipeMarkupParser.DirTextContext;
import recipemd.RecipeMarkupParser.InstructionContext;
import recipemd.RecipeMarkupParser.LinkEntryContext;
import recipemd.RecipeMarkupParser.MeasuredAdditionContext;
import recipemd.RecipeMarkupParser.NoteContext;
import recipemd.RecipeMarkupParser.RecipeContext;
import recipemd.RecipeMarkupParser.TitleEntryContext;

public class MarkdownConverter extends RecipeMarkupBaseListener {

	private class Ingredient {
		String amount;
		String unit;
		String name;
	}

	List<Ingredient> ingredientList = new ArrayList<>();
	AtomicInteger step = new AtomicInteger();
	private final Writer writer;
	StringBuilder directions = new StringBuilder();

	public MarkdownConverter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void enterDirText(DirTextContext ctx) {
		ParseTree child;
		for (int i = 0; i < ctx.getChildCount(); i++) {
			child = ctx.getChild(i);
			bufferDirections(child.getText());
			bufferDirections(" ");
		}
	}

	@Override
	public void exitRecipe(RecipeContext ctx) {
		write("### Ingredients: \n");
		for (Ingredient i : ingredientList) {
			write("* ");
			write(i.amount);
			write(" ");
			if (i.unit != null && !i.unit.equalsIgnoreCase("item")) {
				write(i.unit);
				write(" ");
			}
			write(i.name);
			write("\n");
		}
		write("\n");
		write("### Directions: \n");
		write(directions.toString());
		flush();
	}

	@Override
	public void enterCompDef(CompDefContext ctx) {
		bufferDirections("#### ");
		bufferDirections(getText(ctx.name));
		bufferDirections("\n");
	}

	@Override
	public void exitCompDef(CompDefContext ctx) {
		bufferDirections("\n\n");
	}

	@Override
	public void enterCompRef(CompRefContext ctx) {
		bufferDirections("**");
		bufferDirections(getText(ctx.name));
		bufferDirections("** ");
	}

	@Override
	public void exitCompRef(CompRefContext ctx) {

	}

	@Override
	public void enterInstruction(InstructionContext ctx) {
		bufferDirections(String.valueOf(step.incrementAndGet()));
		bufferDirections(". ");
	}

	@Override
	public void exitInstruction(InstructionContext ctx) {
		bufferDirections("\n");
	}

	@Override
	public void enterMeasuredAddition(MeasuredAdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient);
		i.amount = ctx.amount.getText();
		i.unit = ctx.unit.getText();

		ingredientList.add(i);

		bufferDirections("**");
		bufferDirections(ctx.amount.getText());
		bufferDirections(" ");
		if (!i.unit.equalsIgnoreCase("item")) {
			bufferDirections(ctx.unit.getText());
			bufferDirections(" ");
		}
		bufferDirections(getText(ctx.ingredient));
		bufferDirections("** ");
	}

	@Override
	public void exitMeasuredAddition(MeasuredAdditionContext ctx) {

	}

	@Override
	public void enterAddition(AdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient);
		ingredientList.add(i);
		bufferDirections("**");
		bufferDirections(getText(ctx.ingredient));
		bufferDirections("** ");
	}

	@Override
	public void enterNote(NoteContext ctx) {
		bufferDirections("* ");
	}

	@Override
	public void exitNote(NoteContext ctx) {
		bufferDirections("\n");
	}

	@Override
	public void enterTitleEntry(TitleEntryContext ctx) {
		write("# ");
		for (TerminalNode node : ctx.WORD()) {
			write(node.getText());
			write(" ");
		}
		write("\n");
		write("\n");

	}

	private void write(String text) {
		try {
			if (text != null) {
				writer.write(text);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void bufferDirections(String text) {
		directions.append(text);
	}

	@Override
	public void enterLinkEntry(LinkEntryContext ctx) {
		bufferDirections(ctx.url.getText());
		bufferDirections("\n");
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
