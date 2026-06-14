# .rmd authoring cheat-sheet

Reference for converting web recipes into food-exp `.rmd` files in
`src/main/resources/recipe_files/`. Source of truth is the grammar at
`src/main/antlr4/recipemd/RecipeMarkup.g4` — re-read it if in doubt. See the
project `CLAUDE.md` for the conversion workflow boundaries.

## File skeleton

```
TITLE Recipe Name Here
LINK https://source-url            (optional)
* free-text note / serving info    (optional; STAR + plain text)

# Instruction step with [ingredients] inline.
# Another step...
```

## Ingredient syntax

- **Measured:** `[amount unit [pre-prep] name [post-prep]]`
  e.g. `[1 cup [chopped] onion]`, `[2 cloves garlic[, minced]]`.
- **Unmeasured:** bare `[salt]`, `[lemon wedges]`.
- **Component** (reusable sub-mix / sauce / marinade): define
  `{[Name] # steps... }`, then reference with `$[Name]`.
- **combine block** (long single-step list → renders as a vertical list):
  `combine:{ [a] [b] [c] }`. Literal `combine:` keyword; only bracketed items
  inside, no prose.

## Hard grammar constraints (violations fail to parse)

- **Units are a closed set:** `item sprig stalk tsp tbl cup lbs oz ml g dash
  cloves head gallon`. Map web units: tablespoon→tbl, teaspoon→tsp, pound→lbs,
  clove→cloves, pinch→dash. No `bunch / package / slice / to taste` —
  rework, or use a bare unmeasured `[name]`.
- **Keep volume as volume — never convert to weight.** A `cup` (or tsp/tbl)
  measure maps straight to the `cup` unit; do NOT substitute a guessed gram/oz
  weight. E.g. `4 cups baby arugula` → `[4 cup baby arugula]`, not `[3 oz ...]`.
- **Cans → `oz` via the labeled size.** A can/jar is sized by the weight printed
  on it, so map it to `oz` using that number, multiplying by the count.
  E.g. `2 (14-ounce) cans cannellini beans` → `[28 oz cannellini beans]`;
  `1 (15-ounce) can chickpeas` → `[15 oz chickpeas]`.
- **Unit words are reserved keywords — never use them in prose.** The unit set
  above only tokenizes legally *inside* a bracketed amount. Writing one in
  directions text (e.g. `...and 3 tbl more flour`) fails to parse. So an
  `X plus Y` quantity must be folded into a single bracketed amount: **combine
  the two parts in the smaller unit** (1 tbl = 3 tsp; 1 cup = 16 tbl = 48 tsp).
  Examples: `1 tablespoon plus 1 teaspoon` → `[4 tsp ...]`;
  `1/2 cup plus 3 tablespoons` → `[11 tbl ...]`. If the sum isn't whole, express
  it as a decimal (see next).
- **Amount is ONE token, never a mixed number.** A bracketed amount must be a
  single value: an integer (`3`), a decimal (`1.5`), or a simple proper fraction
  (`1/2`, `2/3`, `1/4`). `1 1/2` is two tokens and will not parse.
  - **Mixed numbers → decimals:** `1 1/2` → `1.5`, `2 3/4` → `2.75`; for thirds
    (no clean decimal) round to two places, `1 1/3` → `1.33`.
  - **Keep simple proper fractions as-is** (`1/2`, `1/3`, `2/3`, `1/4`) — they are
    legal, exact, and read naturally; don't decimalize them.
- **Restricted characters** in words: letters, digits, and only ``. , ( ) : / - ; &``.
  No `%`, `°`, `"`, `½`, etc. — spell out or drop (e.g. `400F`, `165F` are fine;
  drop the degree symbol).
- The `item` unit is dropped in output (prints `4 large eggs`, not `4 item eggs`).

## Conventions

- Bracket a repeated staple (olive oil, salt) only on its first/measured use;
  write later mentions as plain prose so the auto-generated ingredient list
  isn't cluttered with duplicates.
- Split an ingredient used in two stages into two measured additions
  (e.g. garlic used half early / half late → `[5 cloves garlic]` twice).

## Fetching source recipes

WebFetch often returns 403. `curl` with a browser User-Agent plus extracting the
page's `application/ld+json` Recipe block works for many sites, but some
hard-block with 403/429. Easiest reliable path: ask the
user to paste the recipe text.
