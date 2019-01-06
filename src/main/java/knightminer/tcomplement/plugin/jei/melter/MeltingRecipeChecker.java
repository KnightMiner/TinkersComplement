package knightminer.tcomplement.plugin.jei.melter;

import java.util.ArrayList;
import java.util.List;

import knightminer.tcomplement.library.TCompRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeChecker;

public class MeltingRecipeChecker {
	public static List<MeltingRecipe> getMeltingRecipes() {
		List<MeltingRecipe> recipes = new ArrayList<>();

		// first, add all overrides
		for(MeltingRecipe recipe : TCompRegistry.getAllMeltingOverrides()) {
			if(recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && recipe.input.getInputs().size() > 0) {
				recipes.add(recipe);
			}
		}

		// next, add all normal melting recipes if not hidden by the blacklist or overrides
		for(MeltingRecipe recipe : SmeltingRecipeChecker.getSmeltingRecipes()) {
			if (!TCompRegistry.isSmeltingHidden(recipe)) {
				recipes.add(recipe);
			}
		}

		return recipes;//recipes.stream().map(MeltingRecipeWrapper::new).collect(Collectors.toList());
	}
}
