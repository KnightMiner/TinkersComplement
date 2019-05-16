package knightminer.tcomplement.plugin.jei.highoven.heat;

import java.util.List;
import java.util.stream.Collectors;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.HeatRecipe;

public class HighOvenHeatGetter {
	public static List<HeatRecipe> getHeatRecipes() {
		return TCompRegistry.getAllHeatRecipes().stream()
				.filter(r->r instanceof HeatRecipe)
				.map(r->(HeatRecipe)r)
				.filter(HeatRecipe::isValid)
				.collect(Collectors.toList());
	}
}
