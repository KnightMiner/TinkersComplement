package knightminer.knightsconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import knightminer.knightsconstruct.plugin.exnihilo.ExNihiloPlugin;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import slimeknights.tconstruct.tools.TinkerMaterials;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {}

	@Override
	public void register(IModRegistry registry) {
		// add the sledge hammer as an item option for Ex Nihilo hammer smashing
		if(ExNihiloPlugin.sledgeHammer != null) {
			registry.addRecipeCategoryCraftingItem(
					ExNihiloPlugin.sledgeHammer.buildItem(ImmutableList.of(
							TinkerMaterials.wood,
							TinkerMaterials.iron,
							TinkerMaterials.paper
							)),
					"exnihiloadscensio:hammer");
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}

}
