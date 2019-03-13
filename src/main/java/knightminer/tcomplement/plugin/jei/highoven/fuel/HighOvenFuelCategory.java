package knightminer.tcomplement.plugin.jei.highoven.fuel;

import javax.annotation.Nonnull;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.plugin.jei.highoven.mix.HighOvenMixCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class HighOvenFuelCategory implements IRecipeCategory<HighOvenFuelWrapper> {

	public static final String CATEGORY = Util.resource("high_oven_fuel");
	public static final ResourceLocation BACKGROUND = HighOvenMixCategory.BACKGROUND;

	protected final IDrawable background;
	public HighOvenFuelCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND, 77, 86, 18, 34, 0, 0, 0, 88);
	}

	@Override
	public String getModName() {
		return TinkersComplement.modName;
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.high_oven.fuel.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HighOvenFuelWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		items.init(0, true, 0, 16);
		items.set(ingredients);
	}
}
