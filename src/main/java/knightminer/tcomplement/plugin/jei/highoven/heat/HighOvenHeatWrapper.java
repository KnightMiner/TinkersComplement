package knightminer.tcomplement.plugin.jei.highoven.heat;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.library.steelworks.HeatRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

public class HighOvenHeatWrapper implements IRecipeWrapper {

	private FluidStack inputFluid;
	private FluidStack outputFluid;
	private int temp;
	public HighOvenHeatWrapper(HeatRecipe recipe) {
		// fluids
		this.inputFluid = recipe.getInput();
		this.outputFluid = recipe.getOutput();
		this.temp = recipe.getTemperature();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(FluidStack.class, inputFluid);
		ingredients.setOutput(FluidStack.class, outputFluid);
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		String tmpStr = Util.celsiusString(temp);
		int x = 80 - minecraft.fontRenderer.getStringWidth(tmpStr) / 2;
		minecraft.fontRenderer.drawString(tmpStr, x, 8, Util.getHighOvenTempColor(temp));
	}
}
