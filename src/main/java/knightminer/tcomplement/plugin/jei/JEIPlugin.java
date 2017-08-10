package knightminer.tcomplement.plugin.jei;

import knightminer.tcomplement.feature.ModuleFeature;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;


@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	private static final String TINKERS_SMELTERY = "tconstruct.smeltery";

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {}

	@Override
	public void register(IModRegistry registry) {
		// smeltery alternatives
		if(ModuleFeature.melter != null) {
			registry.addRecipeCatalyst(new ItemStack(ModuleFeature.melter), TINKERS_SMELTERY);
		}
		if(ModuleFeature.porcelainMelter != null) {
			registry.addRecipeCatalyst(new ItemStack(ModuleFeature.porcelainMelter), TINKERS_SMELTERY);
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}

}
