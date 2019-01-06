package knightminer.tcomplement.plugin.jei.melter;

import java.util.List;

import javax.annotation.Nonnull;

import knightminer.tcomplement.plugin.jei.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeWrapper;

public class MeltingRecipeWrapper extends SmeltingRecipeWrapper {

	protected boolean isSolid;
	public MeltingRecipeWrapper(MeltingRecipe recipe) {
		super(recipe);
		// if true, we can use solid fuels
		isSolid = recipe.getTemperature() <= 500;
	}

	public List<FluidStack> getLiquidFuels() {
		return fuels;
	}


	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		// if solid fuel is available, draw the flame icon, otherwise cover the slot
		if(isSolid) {
			JEIPlugin.meltingCategory.flame.draw(minecraft, 8, 7);
		} else {
			JEIPlugin.meltingCategory.solidCover.draw(minecraft, 6, 7);
		}
		super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
	}
}
