package knightminer.tcomplement.plugin.jei;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import knightminer.tcomplement.library.TCompRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class MeltingRecipeGetter {
	private static boolean isValid(MeltingRecipe recipe) {
		return recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && !recipe.input.getInputs().isEmpty();
	}

	/** Gets recipes for the melter */
	public static List<MeltingRecipe> getMelterRecipes(List<MeltingRecipe> smelteryRecipes) {
		return Stream.concat(
				TCompRegistry.getAllMeltingOverrides().stream().filter(MeltingRecipeGetter::isValid),
				smelteryRecipes.stream().filter((r)->!TCompRegistry.isSmeltingHidden(r))
				).collect(Collectors.toList());
	}

	/** Gets recipes for the high oven */
	public static List<MeltingRecipe> getHighOvenRecipes(List<MeltingRecipe> smelteryRecipes) {
		return Stream.concat(
				TCompRegistry.getAllHighOvenOverrides().stream().filter(MeltingRecipeGetter::isValid),
				smelteryRecipes.stream().filter((r)->!TCompRegistry.isOvenHidden(r))
				).collect(Collectors.toList());
	}
}
