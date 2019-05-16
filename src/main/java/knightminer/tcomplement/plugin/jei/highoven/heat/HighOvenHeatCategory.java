package knightminer.tcomplement.plugin.jei.highoven.heat;

import javax.annotation.Nonnull;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.Util;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class HighOvenHeatCategory implements IRecipeCategory<HighOvenHeatWrapper> {

	public static final String CATEGORY = Util.resource("high_oven_heat");
	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/jei/high_oven.png");
	public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/high_oven.png");

	protected final IDrawable background;

	public HighOvenHeatCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND, 0, 124, 160, 62);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.high_oven.heat.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		//arrow.draw(minecraft, 73, 24);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HighOvenHeatWrapper recipe, IIngredients ingredients) {
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

		// fluids
		fluids.init(0, true, 20, 22, 18, 18, 1, false, null);
		fluids.init(1, false, 122, 22, 18, 18, 1, false, null);
		fluids.set(ingredients);
	}

	@Override
	public String getModName() {
		return TinkersComplement.modName;
	}
}
