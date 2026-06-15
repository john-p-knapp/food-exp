package recipemd.dynalist;

import java.util.ArrayList;
import java.util.List;

import recipemd.RecipeMarkupBaseListener;
import recipemd.RecipeMarkupParser.AdditionContext;
import recipemd.RecipeMarkupParser.MeasuredAdditionContext;
import recipemd.RecipeMarkupParser.WordContext;

public class IngredientCollector extends RecipeMarkupBaseListener {

	public class Ingredient {
		String amount;
		String unit;
		String name;
	}

	List<Ingredient> ingredientList = new ArrayList<>();

	public List<Ingredient> getIngredientList() {
		return ingredientList;
	}

	@Override
	public void enterAddition(AdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient).toLowerCase();
		ingredientList.add(i);

	}

	@Override
	public void enterMeasuredAddition(MeasuredAdditionContext ctx) {
		Ingredient i = new Ingredient();
		i.name = getText(ctx.ingredient).toLowerCase();
		i.amount = ctx.amount.getText();
		i.unit = ctx.unit.getText();

		ingredientList.add(i);

	}

	private String getText(List<WordContext> words) {
		WordContext w;
		StringBuilder sb = new StringBuilder();
		int size = words.size();
		for (int i = 0; i < size; i++) {
			w = words.get(i);
			sb.append(w.getText());
			if (i < size - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

}
