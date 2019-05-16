package knightminer.tcomplement.library.steelworks;

import net.minecraftforge.fluids.*;

import javax.annotation.*;

public class HighOvenFilter implements IHighOvenFilter {

    private FluidStack input, output;

    /**
     * Filter for the high oven tank.
     * This class is rarely used directly, instead acting as a common superclass
     * @param input   Input fluid, determines primary fluid in tank
     * @param output  Output fluid, a result of the input that is allowed in the tank
     */
    public HighOvenFilter(@Nonnull FluidStack input, @Nonnull FluidStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(FluidStack input, FluidStack output) {
        return (output == null || this.output.isFluidEqual(output))
                && (input == null || this.input.isFluidEqual(input));
    }

    /** JEI */

    /**
     * Checks if a recipe has nonnull inputs and outputs and either undefined or not empty additives
     * @return  True if the recipe is valid
     */
    public boolean isValid() {
        return input != null && input.getFluid() != null && output != null && output.getFluid() != null;
    }

    /** Gets the output fluid stack */
    public FluidStack getOutput() {
        return output;
    }

    /** Gets the input fluid stack */
    public FluidStack getInput() {
        return input;
    }
}
