package knightminer.tcomplement.plugin.jei.highoven.fuel;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class HighOvenFuelWrapper implements IRecipeWrapper {

	private final List<List<ItemStack>> inputs;
	private final int time, rate;
	private final IDrawableAnimated flame;
	public HighOvenFuelWrapper(HighOvenFuel recipe, IGuiHelper guiHelper) {
		inputs = ImmutableList.of(recipe.getFuels());
		time = recipe.getTime();
		rate = recipe.getRate();

		IDrawableStatic flameDrawable = guiHelper.createDrawable(HighOvenFuelCategory.BACKGROUND, 163, 0, 14, 14);
		flame = guiHelper.createAnimatedDrawable(flameDrawable, time, IDrawableAnimated.StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		flame.draw(minecraft, 1, 0);
		minecraft.fontRenderer.drawString(Util.translateFormatted("gui.jei.high_oven.fuel.rate", rate), 24, 13, Color.gray.getRGB());
		minecraft.fontRenderer.drawString(Util.translateFormatted("gui.jei.high_oven.fuel.time", time), 24, 23, Color.gray.getRGB());
	}
}
