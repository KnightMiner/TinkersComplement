package knightminer.tcomplement.plugin.jei.highoven.mix;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.Util;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.Material;

public class HighOvenMixCategory implements IRecipeCategory<HighOvenMixWrapper> {

	public static final String CATEGORY = Util.resource("high_oven_mix");
	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/jei/high_oven.png");
	public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/high_oven.png");

	protected final IDrawable background;
	private final IDrawable scalaInput;
	private final IDrawable scalaOutput;
	private final IDrawableAnimated progress;

	public HighOvenMixCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND, 0, 0, 160, 62);
		scalaInput = guiHelper.createDrawable(BACKGROUND, 160, 68, 16, 52);
		scalaOutput = guiHelper.createDrawable(BACKGROUND, 160, 16, 35, 52);

		IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 160, 0, 3, 16);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.high_oven.mix.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		//arrow.draw(minecraft, 73, 24);
		progress.draw(minecraft, 5, 23);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HighOvenMixWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

		// consumption chance tooltips
		items.addTooltipCallback(recipe);

		// additives
		items.init(0, true, 73, 4);
		items.init(1, true, 73, 22);
		items.init(2, true, 73, 40);
		// representative item input
		items.init(3, true, 8, 22);
		items.set(ingredients);

		// fluids
		fluids.init(0, true, 54, 5, 16, 52, Material.VALUE_Block, false, scalaInput);
		fluids.init(1, false, 120, 5, 35, 52, Material.VALUE_Block, false, scalaOutput);
		fluids.set(ingredients);
	}

	@Override
	public IDrawable getIcon() {
		// use the default icon
		return null;
	}

	@Override
	public String getModName() {
		return TConstruct.modName;
	}
}
