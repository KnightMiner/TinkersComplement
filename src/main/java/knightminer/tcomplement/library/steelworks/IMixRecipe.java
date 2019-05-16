package knightminer.tcomplement.library.steelworks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;

public interface IMixRecipe extends IHighOvenFilter {
	/**
	 * Checks if this recipe matches the given input
	 * @param fluid     Input fluid
	 * @param oxidizer  Input oxidizer
	 * @param reducer   Input reducer
	 * @param purifier  Input purifier
	 * @return  true if the recipe matches, false otherwise
	 */
	boolean matches(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier);

	/**
	 * Applies the recipe to the input, returning the maximum amount
	 * @param fluid  Input fluidstack
	 * @param temp   Current high oven temperature in Celsius, used for minimum temperature requirements
	 * @return  FluidStack result
	 */
	FluidStack getOutput(FluidStack fluid, int temp);

	/**
	 * Gets the normal output of this recipe
	 * @return  FluidStack output
	 */
	default FluidStack getOutput() {
		return null;
	}

	/**
	 * Updates the additives based on the result of the recipe
	 * @param fluid     Input fluidstack, so we know how many times it matched
	 * @param oxidizer  Input oxidizer, may be modified
	 * @param reducer   Input reducer, may be modified
	 * @param purifier  Input purifier, may be modified
	 * @param temp      High oven temperature in Celsius, should NO-OP this method if the value makes {@link #getOutput(FluidStack, int)} return the input
	 */
	void updateAdditives(FluidStack output, ItemStack oxidizer, ItemStack reducer, ItemStack purifier, int temp);

	/**
	 * Adds an oxidizer to this recipe
	 * @param oxidizer  RecipeMatch entry, note amountMatched is used as consumption chance
	 * @return  IMixRecipe instance for chaining
	 */
	IMixRecipe addOxidizer(RecipeMatch oxidizer);

	/**
	 * Adds an oxidizer to this recipe
	 * @param stack    Oxidizer stack
	 * @param consume  Percent chance this oxidizer is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addOxidizer(ItemStack stack, int consume) {
		return addOxidizer(RecipeMatch.of(stack, consume));
	}

	/**
	 * Adds an oxidizer to this recipe
	 * @param stack    Oxidizer oredict name
	 * @param consume  Percent chance this oxidizer is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addOxidizer(String oredict, int consume) {
		return addOxidizer(RecipeMatch.of(oredict, consume));
	}

	/**
	 * Adds an reducer to this recipe
	 * @param reducer  RecipeMatch entry, note amountMatched is used as consumption chance
	 * @return  IMixRecipe instance for chaining
	 */
	IMixRecipe addReducer(RecipeMatch reducer);

	/**
	 * Adds an reducer to this recipe
	 * @param stack    Reducer stack
	 * @param consume  Percent chance this reducer is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addReducer(ItemStack stack, int consume) {
		return addReducer(RecipeMatch.of(stack, consume));
	}

	/**
	 * Adds an reducer to this recipe
	 * @param stack    Reducer oredict name
	 * @param consume  Percent chance this reducer is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addReducer(String oredict, int consume) {
		return addReducer(RecipeMatch.of(oredict, consume));
	}

	/**
	 * Adds an purifier to this recipe
	 * @param purifier  RecipeMatch entry, note amountMatched is used as consumption chance
	 * @return  IMixRecipe instance for chaining
	 */
	IMixRecipe addPurifier(RecipeMatch purifier);

	/**
	 * Adds an purifier to this recipe
	 * @param stack    Purifier stack
	 * @param consume  Percent chance this purifier is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addPurifier(ItemStack stack, int consume) {
		return addPurifier(RecipeMatch.of(stack, consume));
	}

	/**
	 * Adds an purifier to this recipe
	 * @param stack    Purifier oredict name
	 * @param consume  Percent chance this purifier is consumed on this operation
	 * @return  IMixRecipe instance for chaining
	 */
	default IMixRecipe addPurifier(String oredict, int consume) {
		return addPurifier(RecipeMatch.of(oredict, consume));
	}
}
