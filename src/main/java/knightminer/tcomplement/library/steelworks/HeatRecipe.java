package knightminer.tcomplement.library.steelworks;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;

public class HeatRecipe extends HighOvenFilter implements IHeatRecipe {

	private int minTemp;

	/**
	 * Recipe to convert a fluid into another based on the temperature of the high oven
	 * @param input   Input fluid, size determines input consumed per tick at the base temperature
	 * @param output  Fluid result, size determines output produced per tick at the base temperature
	 * @param temp    Minimum temperature for this transformation in Kelvin
	 */
	public HeatRecipe(@Nonnull FluidStack input, @Nonnull FluidStack output, int temp) {
		// high oven processes fluids 4 times per second at 10 times this value with 20 ticks per second
		// x*10*4 = 20 => x = 1/2
		super(new FluidStack(input, input.amount / 2), new FluidStack(output, output.amount / 2));

		// convert to celsius, we use that everywhere anyways
		// parameter is only in kelvin for the sake of consistency with the TiC API
		this.minTemp = temp - 300;
	}

	@Override
	public int timesMatched(FluidStack input, int temp) {
		// if the temperature is too low, do nothing
		if (temp < minTemp) {
			return 0;
		}

		// current input limits the number of matches, otherwise increase time into
		return Math.min(10 * temp / minTemp, input.amount / this.getInput().amount);
	}

	/**
	 * Gets the temperature
	 * @return temperature in Celsius
	 */
	public int getTemperature() {
		return minTemp;
	}
}
