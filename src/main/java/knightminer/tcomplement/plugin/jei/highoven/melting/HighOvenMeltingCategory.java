package knightminer.tcomplement.plugin.jei.highoven.melting;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.plugin.jei.highoven.mix.HighOvenMixCategory;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.Material;

public class HighOvenMeltingCategory implements IRecipeCategory<HighOvenMeltingWrapper> {

	public static final ResourceLocation BACKGROUND = HighOvenMixCategory.BACKGROUND;
	public static final String CATEGORY = Util.resource("high_oven_melting");
	private static List<ItemStack> highOvenFuels;

	private final IDrawable background;
	private final IDrawable scala;
	private final IDrawableAnimated flame;

	public HighOvenMeltingCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND, 0, 62, 160, 62);
		scala = guiHelper.createDrawable(BACKGROUND, 160, 16, 35, 52);

		IDrawableStatic flameDrawable = guiHelper.createDrawable(BACKGROUND, 163, 0, 14, 14);
		flame = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.high_oven.melting.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		flame.draw(minecraft, 78, 22);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HighOvenMeltingWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		items.init(0, true, 20, 22);
		items.set(ingredients);

		// if solid fuels are available, add a standard subset
		items.init(1, true, 77, 38);
		items.set(1, getHighOvenFuels());

		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.addTooltipCallback(GuiUtil::onFluidTooltip);

		fluids.init(0, false, 120, 5, 35, 52, Material.VALUE_Block, false, scala);
		fluids.set(ingredients);
	}

	@Override
	public String getModName() {
		return TinkersComplement.modName;
	}

	public static List<ItemStack> getHighOvenFuels() {
		// get the first of each fuel registered
		if (highOvenFuels != null) {
			return highOvenFuels;
		}
		// get the first item in each fuel entry
		return highOvenFuels = TCompRegistry.getAllHighOvenFuels().stream()
				.map((fuel) -> fuel.getFuels().stream().findFirst().orElse(ItemStack.EMPTY))
				.filter((s)->!s.isEmpty())
				.collect(Collectors.toList());
	}
}
