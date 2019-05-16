package knightminer.tcomplement.library.steelworks;

import net.minecraftforge.fluids.FluidStack;

public interface IHeatRecipe extends IHighOvenFilter {
	/**
	 * Checks if this recipe matches the given input
	 * @param input  FluidStack input
	 * @return  True if it matches
	 */
	default boolean matches(FluidStack input) {
		return matches(input, null);
	}

	/**
	 * Determines the number of times this recipe matches
	 * @param input  Input fluid
	 * @param temp   Current temperature in Celsius, may adjust rate of output
	 * @return  integer denoting the number of times matches
	 */
	int timesMatched(FluidStack input, int temp);

	/**
	 * Gets the normal output of this recipe
	 * @return  FluidStack output
	 */
	default FluidStack getOutput() {
		return null;
	}

	/**
	 * Gets the normal output of this mix recipe
	 * @return  FluidStack output
	 */
	default FluidStack getInput() {
		return null;
	}
}
