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
import recipemd.RecipeMarkupParser.CombinationContext;
import recipemd.RecipeMarkupParser.CompDefContext;
import recipemd.RecipeMarkupParser.CompRefContext;
import recipemd.RecipeMarkupParser.DirTextContext;
import recipemd.RecipeMarkupParser.ImageEntryContext;
import recipemd.RecipeMarkupParser.InstructionContext;
import recipemd.RecipeMarkupParser.LinkEntryContext;
import recipemd.RecipeMarkupParser.MeasuredAdditionContext;
import recipemd.RecipeMarkupParser.MeasuredCompRefContext;
import recipemd.RecipeMarkupParser.NoteContext;
import recipemd.RecipeMarkupParser.RecipeContext;
import recipemd.RecipeMarkupParser.TagEntryContext;
import recipemd.RecipeMarkupParser.TitleEntryContext;

public class MarkdownConverter extends RecipeMarkupBaseListener {

	private class Ingredient {
		String amount;
		String unit;
		String name;
	}

	private final Writer writer;

	private List<Ingredient> ingredientList = new ArrayList<>();
	private AtomicInteger step = new AtomicInteger();
	private StringBuilder buffer;
	private boolean inCombineBlock = false;

	boolean buffering = false;

	public MarkdownConverter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void enterAddition(AdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient);
		ingredientList.add(i);
		write("**");
		write(getText(ctx.ingredient));
		write("** ");
	}

	@Override
	public void enterCombination(CombinationContext ctx) {
		inCombineBlock = true;
		write("combine:");
		softbreak();
		super.enterCombination(ctx);
	}

	@Override
	public void enterCompDef(CompDefContext ctx) {
		startBuffering();
		write("\n#### ");
		write(getText(ctx.name));
		write(" {\n");
	}

	@Override
	public void enterCompRef(CompRefContext ctx) {

		write("**");
		write(getText(ctx.name));
		write("** ");

	}

	@Override
	public void enterDirText(DirTextContext ctx) {
		ParseTree child;
		for (int i = 0; i < ctx.getChildCount(); i++) {
			child = ctx.getChild(i);
			write(child.getText());
			write(" ");
		}
	}

	@Override
	public void enterImageEntry(ImageEntryContext ctx) {
		write("![image](");
		write(ctx.path.getText());
		write(")\n");
		super.enterImageEntry(ctx);
	}

	@Override
	public void enterInstruction(InstructionContext ctx) {
		startBuffering();
		write(String.valueOf(step.incrementAndGet()));
		write(". ");
	}

	@Override
	public void enterLinkEntry(LinkEntryContext ctx) {
		write("* ");
		write(ctx.url.getText());
		write("\n");
	}

	@Override
	public void enterMeasuredAddition(MeasuredAdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient);
		i.amount = ctx.amount.getText();
		i.unit = ctx.unit.getText();

		ingredientList.add(i);

		write("**");
		write(ctx.amount.getText());
		write(" ");
		if (!i.unit.equalsIgnoreCase("item")) {
			write(ctx.unit.getText());
			write(" ");
		}

		if (getText(ctx.prep).length() > 0) {
			write(getText(ctx.prep));
			write(" ");
		}

		write(getText(ctx.ingredient));

		if (getText(ctx.postprep).length() > 0) {
			write(" ");
			write(getText(ctx.postprep));
		}

		write("** ");
	}

	@Override
	public void enterMeasuredCompRef(MeasuredCompRefContext ctx) {

		write("**");
		write(ctx.amount.getText());
		write(" ");
		if (!ctx.unit.getText().equalsIgnoreCase("item")) {
			write(ctx.unit.getText());
			write(" ");
		}

		write(getText(ctx.name));

		write("** ");
		super.enterMeasuredCompRef(ctx);
	}

	@Override
	public void enterNote(NoteContext ctx) {
		write("* ");
	}

	@Override
	public void enterTagEntry(TagEntryContext ctx) {

		ParseTree child;
		write("Tags: ");
		for (int i = 1; i < ctx.getChildCount(); i++) {
			child = ctx.getChild(i);
			write(" #");
			write(child.getText());
		}
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

	@Override
	public void exitAddition(AdditionContext ctx) {
		if (inCombineBlock) {
			softbreak();
		}
	}

	@Override
	public void exitCombination(CombinationContext ctx) {
		inCombineBlock = false;
		write("\n");
	}

	@Override
	public void exitCompDef(CompDefContext ctx) {
		write("\n}\n\n");
		step.set(0);
	}

	@Override
	public void exitCompRef(CompRefContext ctx) {
		if (inCombineBlock) {
			softbreak();
		}
	}

	@Override
	public void exitInstruction(InstructionContext ctx) {
		write("\n");
	}

	@Override
	public void exitMeasuredAddition(MeasuredAdditionContext ctx) {
		if (inCombineBlock) {
			softbreak();
		}
	}

	private void softbreak() {
		write("  \n");
	}

	@Override
	public void exitMeasuredCompRef(MeasuredCompRefContext ctx) {
		if (inCombineBlock) {
			softbreak();
		}
	}

	@Override
	public void exitNote(NoteContext ctx) {
		write("\n");
	}

	@Override
	public void exitRecipe(RecipeContext ctx) {
		stopBuffering();
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
		write(buffer.toString());

		flush();
	}

	private void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	private void startBuffering() {

		if (buffering == false) {
			buffering = true;
			buffer = new StringBuilder();
		}

	}

	private void stopBuffering() {
		buffering = false;

	}

	private void write(String text) {
		try {
			if (text != null) {
				if (buffering) {
					buffer.append(text);
				} else {
					writer.write(text);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
