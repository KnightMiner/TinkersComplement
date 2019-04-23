package knightminer.tcomplement.library.steelworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.events.TCompRegisterEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;

public class MixRecipe implements IMixRecipe {
	private Map<MixAdditive,MixAdditiveList> additives;
	private FluidStack input, output;
	private static final Random RANDOM = new Random();

	public MixRecipe(@Nonnull FluidStack input, @Nonnull FluidStack output) {
		this.output = output;
		this.input = input;
	}

	private boolean ingredientMatches(MixAdditive type, ItemStack input) {
		return !additives.containsKey(type) || additives.get(type).matches(input).isPresent();
	}

	@Override
	public boolean matches(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		return this.input.isFluidEqual(input) && additives == null || (
				ingredientMatches(MixAdditive.OXIDIZER, oxidizer) &&
				ingredientMatches(MixAdditive.REDUCER, reducer) &&
				ingredientMatches(MixAdditive.PURIFIER, purifier));
	}

	@Override
	public boolean matches(FluidStack input, FluidStack output) {
		return this.output.isFluidEqual(output) && this.input.isFluidEqual(input);
	}

	private void removeMatches(MixAdditive type, ItemStack input, int matched) {
		// additive type not present, so do nothing
		if (additives == null || !additives.containsKey(type)) {
			return;
		}

		// default chance to 100% if the item is missing. Should never happen, but just in case
		int chance = additives.get(type).matches(input).map((m)->m.amount).orElse(100);
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
	public FluidStack getOutput(FluidStack fluid) {
		return new FluidStack(output, fluid.amount * output.amount / input.amount);
	}

	@Override
	public void updateAdditives(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		// determine how many times we outputed
		int matches = fluid.amount / input.amount;
		if (fluid.amount % input.amount > 0) {
			matches++;
		}

		// remove matches from each of the three stacks
		if(matches > 0) {
			removeMatches(MixAdditive.OXIDIZER, oxidizer, matches);
			removeMatches(MixAdditive.REDUCER, reducer, matches);
			removeMatches(MixAdditive.PURIFIER, purifier, matches);
		}
	}

	/* Ingredient methods */
	private void addAdditive(RecipeMatch additive, MixAdditive type) {
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
				TCompRegistry.log.debug("Addition of {} {} to recipe {} has been cancelled by event", type.getName(), input, output.getUnlocalizedName());
			} catch(Exception e) {
				TCompRegistry.log.error("Error when logging HighOvenMixAdditiveEvent", e);
			}
		}
	}

	@Override
	public MixRecipe addOxidizer(RecipeMatch oxidizer) {
		addAdditive(oxidizer, MixAdditive.OXIDIZER);
		return this;
	}
	@Override
	public MixRecipe addReducer(RecipeMatch reducer) {
		addAdditive(reducer, MixAdditive.REDUCER);
		return this;
	}
	@Override
	public MixRecipe addPurifier(RecipeMatch purifier) {
		addAdditive(purifier, MixAdditive.PURIFIER);
		return this;
	}

	/** JEI */

	/**
	 * Checks if a recipe has nonnull inputs and outputs and either undefined or not empty additives
	 * @return  True if the recipe is valid
	 */
	public boolean isValid() {
		// ensure fluids are valid
		if (input == null || input.getFluid() == null || output == null || output.getFluid() == null) {
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

	@Override
	public FluidStack getOutput() {
		return output;
	}

	/** Gets the input fluid stack */
	public FluidStack getInput() {
		return input;
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
