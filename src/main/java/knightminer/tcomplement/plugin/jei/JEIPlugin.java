package knightminer.tcomplement.plugin.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.ModuleFeature;
import knightminer.tcomplement.feature.client.GuiMelter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;


@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	private static final String FURNACE_FUEL = VanillaRecipeCategoryUid.FUEL;
	private static final String TINKERS_SMELTERY = "tconstruct.smeltery";

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

			// liquid recipe lookup
			registry.addAdvancedGuiHandlers(new TinkerGuiTankHandler<>(GuiMelter.class));
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
