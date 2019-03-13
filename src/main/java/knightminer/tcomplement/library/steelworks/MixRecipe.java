package knightminer.tcomplement.library.steelworks;

import java.util.EnumMap;
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
	private Map<MixAdditive,RecipeMatchRegistry> additives;
	private FluidStack input, output;
	private static final Random RANDOM = new Random();

	public MixRecipe(@Nonnull FluidStack output, @Nonnull FluidStack input) {
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
	public boolean matches(FluidStack output, FluidStack input) {
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
				additives.computeIfAbsent(type, (t) -> new RecipeMatchRegistry()).addRecipeMatch(additive);
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

	@Override
	public FluidStack getOutput() {
		return output;
	}
}
