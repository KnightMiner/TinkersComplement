package knightminer.tcomplement.plugin.jei.highoven.mix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class HighOvenMixGetter {
	public static List<HighOvenMixWrapper> getMixRecipes(List<MeltingRecipe> meltingRecipes) {
		// convert the recipes into a map of fluids to two equal size lists of inputs and outputs
		Map<Fluid, List<ItemStack>> itemsForFluid = meltingRecipes.stream()
				.collect(HashMap::new, (map, recipe) -> {
					List<ItemStack> inputs = recipe.input.getInputs();
					if(!inputs.isEmpty()) {
						map.computeIfAbsent(recipe.getResult().getFluid(), (f)->new ArrayList<>()).addAll(inputs);
					}
				}, Map::putAll);

		// find all valid mix recipes and add those inputs in
		List<HighOvenMixWrapper> recipes = new ArrayList<>();
		for(IMixRecipe irecipe : TCompRegistry.getAllMixRecipes()) {
			if (irecipe instanceof MixRecipe) {
				MixRecipe recipe = (MixRecipe)irecipe;
				if(recipe.isValid()) {
					// ensure we have a list of inputs for that fluid
					Fluid input = recipe.getInput().getFluid();
					if(itemsForFluid.containsKey(input)) {
						recipes.add(new HighOvenMixWrapper(recipe, itemsForFluid.get(input)));
					}
				}
			}
		}

		return recipes;
	}
}
