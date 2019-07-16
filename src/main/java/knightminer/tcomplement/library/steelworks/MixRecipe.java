package knightminer.tcomplement.library.steelworks;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.library.events.TCompRegisterEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MixRecipe extends HighOvenFilter implements IMixRecipe {
	private Map<MixAdditive,MixAdditiveList> additives;
	private static final Random RANDOM = new Random();

	private int minTemp;

	/**
	 * Recipe to mix a fluid and additives into a result
	 * @param input   Input fluid, determines rate of additive consumption
	 * @param output  Output fluid, determines rate of output based on input size
	 * @param temp    Minimum temperature to perform this recipe
	 */
	public MixRecipe(@Nonnull FluidStack input, @Nonnull FluidStack output, int temp) {
		super(input, output);

		// convert to celsius, we use that everywhere anyways
		// parameter is only in kelvin for the sake of consistency with the TiC API
		this.minTemp = temp - 300;
	}

	/**
	 * Recipe to mix a fluid and additives into a result
	 * @param input   Input fluid, size determines rate of additive consumption
	 * @param output  Output fluid, size determines rate of output based on input size=
	 */
	public MixRecipe(@Nonnull FluidStack input, @Nonnull FluidStack output) {
		this(input, output, output.getFluid().getTemperature(output));
	}

	private boolean additiveMatches(MixAdditive type, ItemStack input) {
		return !additives.containsKey(type) || additives.get(type).matches(input).isPresent();
	}

	@Override
	public boolean matches(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		return this.getInput().isFluidEqual(fluid) && (additives == null || (
				additiveMatches(MixAdditive.OXIDIZER, oxidizer) &&
				additiveMatches(MixAdditive.REDUCER, reducer) &&
				additiveMatches(MixAdditive.PURIFIER, purifier)));
	}

	private boolean additiveValid(MixAdditive type, ItemStack input, int required) {
		return !additives.containsKey(type) || input.getCount() >= required;
	}

	@Override
	public boolean canMix(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier, int temperature) {
		if (temperature < minTemp) {
			return false;
		}

		if (additives == null) {
			return true;
		}

		int required = Util.ceilDiv(fluid.amount, getInput().amount);
		return additiveValid(MixAdditive.OXIDIZER, oxidizer, required) &&
					 additiveValid(MixAdditive.REDUCER, reducer, required) &&
					 additiveValid(MixAdditive.PURIFIER, purifier, required);
	}

	private void removeMatches(MixAdditive type, ItemStack input, int matched) {
		// additive type not present, so do nothing
		if (additives == null || !additives.containsKey(type)) {
			return;
		}

		// default chance to 100% if the item is missing. Should never happen, but just in case
		int chance = additives.get(type).matches(input).map(m -> m.amount).orElse(100);
		// if 100% chance, just shrink by number
		if (chance >= 100) {
			input.shrink(matched);
		} else {
			// try chance for each match to determine shrinking
			int shrink = 0;
			for(int i = 0; i < matched; i++) {
				if (RANDOM.nextInt(100) < chance) {
					shrink++;
				}
			}
			input.shrink(shrink);
		}
	}

	@Override
	public FluidStack getOutput(FluidStack fluid, int temp) {
		FluidStack output = this.getOutput();
		return new FluidStack(output, fluid.amount * output.amount / this.getInput().amount);
	}

	@Override
	public void updateAdditives(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier, int temp) {
		// determine how many times we outputted
		int matches = Util.ceilDiv(fluid.amount, getInput().amount);

		// remove matches from each of the three stacks
		if(matches > 0) {
			removeMatches(MixAdditive.OXIDIZER, oxidizer, matches);
			removeMatches(MixAdditive.REDUCER, reducer, matches);
			removeMatches(MixAdditive.PURIFIER, purifier, matches);
		}
	}

	/* Ingredient methods */
	@Override
	public void addAdditive(MixAdditive type, RecipeMatch additive) {
		if (additive != null) {
			// fire event so addition of this additive can be canceled
			if(new TCompRegisterEvent.HighOvenMixAdditiveEvent(this, additive, type).fire()) {
				if(additives == null) {
					additives = new EnumMap<>(MixAdditive.class);
				}
				// insert the ingredient
				additives.computeIfAbsent(type, (t) -> new MixAdditiveList()).addRecipeMatch(additive);
				TCompRegistry.registerMixAdditive(additive, type);
			} else try {
				String input = additive.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
				TCompRegistry.log.debug("Addition of {} {} to recipe {} has been cancelled by event", type.getName(), input, getOutput().getUnlocalizedName());
			} catch(Exception e) {
				TCompRegistry.log.error("Error when logging HighOvenMixAdditiveEvent", e);
			}
		}
	}

	/* JEI */

	@Override
	public boolean isValid() {
		if (!super.isValid()) {
			return false;
		}

		// ensure additives are valid
		if (additives != null) {
			for(MixAdditive type : MixAdditive.values()) {
				// if the additive is set, it must have items
				if(additives.containsKey(type) && additives.get(type).getInputs().isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the temperature
	 * @return temperature in Celsius
	 */
	public int getTemperature() {
		return minTemp;
	}

	/**
	 * Gets all additives of a type
	 * @param type  Additive type
	 * @return  List of additives of that type, or an empty list if unused. Note this does not distinguish unused from empty
	 */
	public List<ItemStack> getAdditives(MixAdditive type) {
		if(additives == null || !additives.containsKey(type)) {
			return Collections.emptyList();
		}
		return additives.get(type).getInputs();
	}

	/**
	 * Gets the consumption chance of an additive
	 * @param type   Additive type
	 * @param input  Stack to check
	 * @return  Chance from 0 to 100, or null if stack is not an additive of that type for this recipe
	 */
	public Integer getAdditiveConsumeChance(MixAdditive type, ItemStack input) {
		// additive type not present
		if (additives == null || !additives.containsKey(type)) {
			return null;
		}
		// find it in the list, returning null if missing
		return additives.get(type).matches(input).map((m)->m.amount).orElse(null);
	}

	/** Internal copy of RecipeMatchRegistry to gain access to the internal items list */
	private static class MixAdditiveList extends RecipeMatchRegistry {
		private List<ItemStack> displayItems;
		public List<ItemStack> getInputs() {
			if (displayItems != null) {
				return displayItems;
			}
			return displayItems = items.stream().map(RecipeMatch::getInputs).reduce(new ArrayList<>(), (list, items) -> {
				list.addAll(items);
				return list;
			});
		}
	}
}
