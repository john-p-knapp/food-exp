# food-exp — Claude guidance

This repo captures favorite recipes in a custom `.rmd` markup (ANTLR grammar:
`src/main/antlr4/recipemd/RecipeMarkup.g4`). A pipeline converts `.rmd` files
into Markdown under `recipes/` and into a sorted Dynalist grocery list.

## Converting web recipes to .rmd

When asked to convert a recipe into `.rmd`, write the file to
`src/main/resources/recipe_files/<slug>.rmd`. Boundaries:

- **Do NOT check or update `grocery_sort.csv`.** The maintainer does this
  explicitly, on request only.
- **Prefer pasted recipe text.** Many recipe sites block automated fetches
  (403/429); ask the user to paste rather than fighting bot protection.

## Authoring style

- For a single step that incorporates a long list of ingredients, use the
  grammar's `combine:{ [a] [b] [c] }` block (renders as a vertical list)
  rather than an inline comma-separated run.

The full grammar cheat-sheet (unit set, parse constraints, conventions) is in
@docs/rmd-authoring.md
