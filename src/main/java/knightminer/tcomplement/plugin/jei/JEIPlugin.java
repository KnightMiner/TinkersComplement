package knightminer.tcomplement.plugin.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.melter.ModuleMelter;
import knightminer.tcomplement.melter.client.GuiMelter;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import knightminer.tcomplement.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcomplement.plugin.jei.melter.MeltingRecipeCategory;
import knightminer.tcomplement.plugin.jei.melter.MeltingRecipeChecker;
import knightminer.tcomplement.plugin.jei.melter.MeltingRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;
import slimeknights.tconstruct.tools.TinkerMaterials;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	private static final String FURNACE_FUEL = VanillaRecipeCategoryUid.FUEL;
	private static final String TINKERS_SMELTERY = "tconstruct.smeltery";
	private static final String TINKERS_ALLOYING = "tconstruct.alloy";
	private static final String EXNIHILO_HAMMER = "exnihilocreatio:hammer";
	private static final String CHISEL_CHISELING = "chisel.chiseling";
	public static MeltingRecipeCategory meltingCategory;

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		// Melter
		if(Config.jei.separateMelterTab && TinkersComplement.pulseManager.isPulseLoaded(ModuleMelter.pulseID)) {
			registry.addRecipeCategories(meltingCategory = new MeltingRecipeCategory(guiHelper));
		}
	}

	@Override
	public void register(IModRegistry registry) {
		if(TinkersComplement.pulseManager.isPulseLoaded(ModuleMelter.pulseID)) {
			String melterCategory = TINKERS_SMELTERY;
			if(Config.jei.separateMelterTab) {
				melterCategory = MeltingRecipeCategory.CATEGORY;
				registry.handleRecipes(MeltingRecipe.class, MeltingRecipeWrapper::new, MeltingRecipeCategory.CATEGORY);
				registry.addRecipes(MeltingRecipeChecker.getMeltingRecipes(), MeltingRecipeCategory.CATEGORY);
			}
			// smeltery alternatives
			if(ModuleMelter.melter != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.melter), melterCategory);
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.melter, 1, 8), FURNACE_FUEL);
			}
			if(ModuleMelter.porcelainMelter != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.porcelainMelter), melterCategory);
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.porcelainMelter, 1, 8), FURNACE_FUEL);
			}
			if(ModuleMelter.alloyTank != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.alloyTank), TINKERS_ALLOYING);
			}
			if(ModuleMelter.porcelainAlloyTank != null) {
				registry.addRecipeCatalyst(new ItemStack(ModuleMelter.porcelainAlloyTank), TINKERS_ALLOYING);
			}

			// liquid recipe lookup
			registry.addAdvancedGuiHandlers(new TinkerGuiTankHandler<>(GuiMelter.class));
		}
		// add our chisel to the chisel chisel group
		if(ChiselPlugin.chisel != null) {
			registry.addRecipeCatalyst(
					ChiselPlugin.chisel.buildItem(ImmutableList.of(
							TinkerMaterials.wood,
							TinkerMaterials.iron
							)),
					CHISEL_CHISELING);
		}
		// add our hammer to the ex nihilo hammer group
		if(ExNihiloPlugin.sledgeHammer != null) {
			registry.addRecipeCatalyst(
					ExNihiloPlugin.sledgeHammer.buildItem(ImmutableList.of(
							TinkerMaterials.wood,
							TinkerMaterials.iron,
							TinkerMaterials.paper
							)),
					EXNIHILO_HAMMER);
		}
	}


	private static class TinkerGuiTankHandler<T extends GuiContainer & IGuiLiquidTank> implements IAdvancedGuiHandler<T> {
		private Class<T> clazz;

		public TinkerGuiTankHandler(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Nonnull
		@Override
		public Class<T> getGuiContainerClass() {
			return clazz;
		}

		@Nullable
		@Override
		public Object getIngredientUnderMouse(T guiContainer, int mouseX, int mouseY) {
			return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
		}
	}
}
