package knightminer.tcomplement.plugin.jei.highoven.melting;

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
		int temp = temperature-300;
		String tmpStr = String.valueOf(temp) + "\u00B0c";
		int x = 86 - minecraft.fontRenderer.getStringWidth(tmpStr) / 2;
		minecraft.fontRenderer.drawString(tmpStr, x, 10, Util.getHighOvenTempColor(temp));
	}
}
