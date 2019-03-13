package knightminer.tcomplement.plugin.jei;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.melter.MelterModule;
import knightminer.tcomplement.melter.client.GuiMelter;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import knightminer.tcomplement.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcomplement.plugin.jei.highoven.fuel.HighOvenFuelCategory;
import knightminer.tcomplement.plugin.jei.highoven.fuel.HighOvenFuelGetter;
import knightminer.tcomplement.plugin.jei.highoven.fuel.HighOvenFuelWrapper;
import knightminer.tcomplement.plugin.jei.highoven.melting.HighOvenMeltingCategory;
import knightminer.tcomplement.plugin.jei.highoven.melting.HighOvenMeltingWrapper;
import knightminer.tcomplement.plugin.jei.highoven.mix.HighOvenMixCategory;
import knightminer.tcomplement.plugin.jei.highoven.mix.HighOvenMixGetter;
import knightminer.tcomplement.plugin.jei.highoven.mix.HighOvenMixWrapper;
import knightminer.tcomplement.plugin.jei.melter.MeltingRecipeCategory;
import knightminer.tcomplement.plugin.jei.melter.MeltingRecipeWrapper;
import knightminer.tcomplement.steelworks.SteelworksModule;
import knightminer.tcomplement.steelworks.client.GuiHighOven;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeChecker;
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
		if(Config.jei.separateMelterTab && PulseBase.isMelterLoaded()) {
			registry.addRecipeCategories(meltingCategory = new MeltingRecipeCategory(guiHelper));
		}

		// High Oven
		if(PulseBase.isSteelworksLoaded()) {
			registry.addRecipeCategories(new HighOvenMixCategory(guiHelper));
			registry.addRecipeCategories(new HighOvenFuelCategory(guiHelper));
			if(Config.jei.separateHighOvenTab) {
				registry.addRecipeCategories(new HighOvenMeltingCategory(guiHelper));
			}
		}
	}

	@Override
	public void register(IModRegistry registry) {
		List<MeltingRecipe> smelteryRecipes = null;
		if(PulseBase.isMelterLoaded()) {
			String melterCategory = TINKERS_SMELTERY;
			if(Config.jei.separateMelterTab) {
				melterCategory = MeltingRecipeCategory.CATEGORY;
				smelteryRecipes = SmeltingRecipeChecker.getSmeltingRecipes();
				registry.handleRecipes(MeltingRecipe.class, MeltingRecipeWrapper::new, MeltingRecipeCategory.CATEGORY);
				registry.addRecipes(MeltingRecipeGetter.getMelterRecipes(smelteryRecipes), MeltingRecipeCategory.CATEGORY);
			}
			// smeltery alternatives
			if(MelterModule.melter != null) {
				registry.addRecipeCatalyst(new ItemStack(MelterModule.melter), melterCategory);
				registry.addRecipeCatalyst(new ItemStack(MelterModule.melter, 1, 8), FURNACE_FUEL);
			}
			if(MelterModule.porcelainMelter != null) {
				registry.addRecipeCatalyst(new ItemStack(MelterModule.porcelainMelter), melterCategory);
				registry.addRecipeCatalyst(new ItemStack(MelterModule.porcelainMelter, 1, 8), FURNACE_FUEL);
			}
			if(MelterModule.alloyTank != null) {
				registry.addRecipeCatalyst(new ItemStack(MelterModule.alloyTank), TINKERS_ALLOYING);
			}
			if(MelterModule.porcelainAlloyTank != null) {
				registry.addRecipeCatalyst(new ItemStack(MelterModule.porcelainAlloyTank), TINKERS_ALLOYING);
			}

			// liquid recipe lookup
			registry.addAdvancedGuiHandlers(new TinkerGuiTankHandler<>(GuiMelter.class));
		}

		if(PulseBase.isSteelworksLoaded()) {
			final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

			// Melting recipe separate category
			String highOvenMelting = TINKERS_SMELTERY;

			// skip loading these again if already loaded
			if (smelteryRecipes == null) {
				smelteryRecipes = SmeltingRecipeChecker.getSmeltingRecipes();
			}
			List<MeltingRecipe> highOvenMeltingRecipes = MeltingRecipeGetter.getHighOvenRecipes(smelteryRecipes);
			if(Config.jei.separateHighOvenTab) {
				highOvenMelting = HighOvenMeltingCategory.CATEGORY;
				registry.handleRecipes(MeltingRecipe.class, HighOvenMeltingWrapper::new, highOvenMelting);
				registry.addRecipes(highOvenMeltingRecipes, highOvenMelting);
			}

			// Mix category
			registry.handleRecipes(HighOvenMixWrapper.class, (r)->r, HighOvenMixCategory.CATEGORY);
			registry.addRecipes(HighOvenMixGetter.getMixRecipes(highOvenMeltingRecipes), HighOvenMixCategory.CATEGORY);

			// fuel category
			registry.handleRecipes(HighOvenFuel.class, (fuel)->new HighOvenFuelWrapper(fuel, guiHelper), HighOvenFuelCategory.CATEGORY);
			registry.addRecipes(HighOvenFuelGetter.getHighOvenFuels(), HighOvenFuelCategory.CATEGORY);

			// catalysts
			registry.addRecipeCatalyst(new ItemStack(SteelworksModule.highOvenController),
					highOvenMelting, HighOvenMixCategory.CATEGORY, HighOvenFuelCategory.CATEGORY);

			// liquid recipe lookup
			registry.addAdvancedGuiHandlers(new TinkerGuiTankHandler<>(GuiHighOven.class));
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
