package knightminer.tcomplement.library.steelworks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Empty version of MixRecipe, used to prevent the need for null checks on chaining in case the recipe is canceled
 */
public enum EmptyMixRecipe implements IMixRecipe {
	INSTANCE;

	@Override
	public boolean matches(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		return false;
	}

	@Override
	public boolean matches(FluidStack output, FluidStack input) {
		return false;
	}

	@Override
	public FluidStack getOutput(FluidStack fluid) {
		return fluid;
	}

	@Override
	public void updateAdditives(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {}

	@Override
	public IMixRecipe addOxidizer(RecipeMatch oxidizer) {
		return this;
	}

	@Override
	public IMixRecipe addReducer(RecipeMatch reducer) {
		return this;
	}

	@Override
	public IMixRecipe addPurifier(RecipeMatch purifier) {
		return this;
	}
}
