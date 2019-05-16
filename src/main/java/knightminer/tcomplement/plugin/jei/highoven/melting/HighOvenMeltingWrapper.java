package knightminer.tcomplement.plugin.jei.highoven.melting;

import static slimeknights.tconstruct.library.Util.temperatureString;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.Util;
import net.minecraft.client.Minecraft;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeWrapper;

public class HighOvenMeltingWrapper extends SmeltingRecipeWrapper {

	public HighOvenMeltingWrapper(MeltingRecipe recipe) {
		super(recipe);
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		String tmpStr = temperatureString(temperature);
		int x = 86 - minecraft.fontRenderer.getStringWidth(tmpStr) / 2;
		minecraft.fontRenderer.drawString(tmpStr, x, 8, Util.getHighOvenTempColor(temperature-300));
	}
}
