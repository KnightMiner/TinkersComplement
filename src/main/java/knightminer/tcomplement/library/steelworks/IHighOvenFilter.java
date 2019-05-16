package knightminer.tcomplement.library.steelworks;

import net.minecraftforge.fluids.FluidStack;

public interface IHighOvenFilter {
	/**
	 * Checks if this recipe matches a given input and output pair, for the sake of the tank filters
	 * @param input   FluidStack input, null acts as wildcard
	 * @param output  FluidStack output, null acts as wildcard
	 * @return  True if it matches, false otherwise
	 */
	boolean matches(FluidStack input, FluidStack output);
}
