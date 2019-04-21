package knightminer.tcomplement.plugin.jei.highoven.mix;

import java.util.List;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

public class HighOvenMixWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {

	private MixRecipe recipe;
	private List<List<ItemStack>> inputStacks;
	private FluidStack inputFluid;
	private FluidStack outputFluid;
	public HighOvenMixWrapper(MixRecipe recipe, List<ItemStack> meltingInputs) {
		this.recipe = recipe;
		// fluids
		this.inputFluid = recipe.getInput();
		this.outputFluid = recipe.getOutput();

		// additives
		ImmutableList.Builder<List<ItemStack>> stacks = ImmutableList.builder();
		for(MixAdditive type : MixAdditive.values()) {
			stacks.add(recipe.getAdditives(type));
		}
		stacks.add(meltingInputs);
		this.inputStacks = stacks.build();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputStacks);
		ingredients.setInput(FluidStack.class, inputFluid);
		ingredients.setOutput(FluidStack.class, outputFluid);
	}

	@Override
	public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
		// representative item slot
		if(slotIndex == 3) {
			tooltip.add(Util.translateFormatted("gui.jei.high_oven.mix.represenative", inputFluid.getLocalizedName()));
			return;
		} else if(slotIndex <= 2) {
			Integer chance = recipe.getAdditiveConsumeChance(MixAdditive.fromIndex(slotIndex), ingredient);
			if(chance != null) {
				tooltip.add(String.format("%s%s%s", TextFormatting.GRAY, TextFormatting.ITALIC, Util.translateFormatted("gui.jei.high_oven.mix.consume", chance)));
			}
		}
	}
}
