package knightminer.tcomplement.plugin.jei.melter;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.TinkersComplement;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.Material;

public class MeltingRecipeCategory implements IRecipeCategory<MeltingRecipeWrapper> {

	public static final String CATEGORY = Util.resource("melter");
	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/jei/melter.png");
	private static List<ItemStack> furnaceFuels;

	private final IDrawable background;
	protected final IDrawable solidCover;
	protected final IDrawableAnimated flame;
	private final IDrawableAnimated progress;

	public MeltingRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND, 0, 0, 160, 46, 0, 0, 0, 0);
		solidCover = guiHelper.createDrawable(BACKGROUND, 174, 0, 18, 33);

		IDrawableStatic flameDrawable = guiHelper.createDrawable(BACKGROUND, 160, 0, 14, 14);
		flame = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);
		IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 160, 14, 3, 16);
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
		return Util.translate("gui.jei.melter.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		progress.draw(minecraft, 49, 21);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MeltingRecipeWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		items.init(0, true, 52, 20);
		items.set(ingredients);

		// if solid fuels are available, add a standard subset
		if(recipe.isSolid) {
			items.init(1, true, 6, 22);
			items.set(1, getFurnaceFuels());
		}

		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.addTooltipCallback(GuiUtil::onFluidTooltip);

		fluids.init(0, false, 121, 7, 32, 32, Material.VALUE_Block, false, null);
		fluids.set(ingredients);

		fluids.init(1, true, 29, 7, 12, 32, 1000, false, null);
		fluids.set(1, recipe.getLiquidFuels());
	}

	@Override
	public String getModName() {
		return TinkersComplement.modName;
	}

	private static List<ItemStack> getFurnaceFuels() {
		if (furnaceFuels != null) {
			return furnaceFuels;
		}
		return furnaceFuels = ImmutableList.of(new ItemStack(Items.COAL), new ItemStack(Items.COAL, 1, 1), new ItemStack(Blocks.LOG), new ItemStack(Blocks.PLANKS), new ItemStack(Blocks.COAL_BLOCK));
	}
}
