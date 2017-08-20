package knightminer.tcomplement.plugin.jei;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.ModuleFeature;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;


@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	private static final String FURNACE_FUEL = VanillaRecipeCategoryUid.FUEL;
	private static final String TINKERS_SMELTERY = "tconstruct.smeltery";

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {}

	@Override
	public void register(IModRegistry registry) {
		if(TinkersComplement.pulseManager.isPulseLoaded(ModuleFeature.pulseID)) {
			// smeltery alternatives
			if(ModuleFeature.melter != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleFeature.melter), TINKERS_SMELTERY);
				registry.addRecipeCatalyst(new ItemStack(ModuleFeature.melter, 1, 8), FURNACE_FUEL);
			}
			if(ModuleFeature.porcelainMelter != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleFeature.porcelainMelter), TINKERS_SMELTERY);
				registry.addRecipeCatalyst(new ItemStack(ModuleFeature.porcelainMelter, 1, 8), FURNACE_FUEL);
			}
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}

}
