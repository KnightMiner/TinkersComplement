package knightminer.tcomplement.library.events;

import knightminer.tcomplement.library.IBlacklist;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public abstract class TCompRegisterEvent<T> extends TinkerRegisterEvent<T> {

	public TCompRegisterEvent(T recipe) {
		super(recipe);
	}

	/** Register a melting override */
	public static class MelterOverrideRegisterEvent extends TCompRegisterEvent<MeltingRecipe> {
		public MelterOverrideRegisterEvent(MeltingRecipe recipe) {
			super(recipe);
		}
	}

	/** Register a melting blacklist entry */
	public static class MelterBlackListRegisterEvent extends TCompRegisterEvent<IBlacklist> {
		public MelterBlackListRegisterEvent(IBlacklist recipe) {
			super(recipe);
		}
	}

	/* High Oven */

	/** Register a high oven override */
	public static class HighOvenOverrideRegisterEvent extends TCompRegisterEvent<MeltingRecipe> {
		public HighOvenOverrideRegisterEvent(MeltingRecipe recipe) {
			super(recipe);
		}
	}

	/** Register a high oven mix recipe */
	public static class HighOvenFuelRegisterEvent extends TCompRegisterEvent<HighOvenFuel> {
		public HighOvenFuelRegisterEvent(HighOvenFuel recipe) {
			super(recipe);
		}
	}

	/** Register a high oven mix recipe */
	public static class HighOvenMixRegisterEvent extends TCompRegisterEvent<IMixRecipe> {
		public HighOvenMixRegisterEvent(IMixRecipe recipe) {
			super(recipe);
		}
	}

	/** Adds an additive to a High Oven Mix recipe */
	public static class HighOvenMixAdditiveEvent extends TCompRegisterEvent<MixRecipe> {
		private RecipeMatch additive;
		private MixAdditive type;
		public HighOvenMixAdditiveEvent(MixRecipe mixRecipe, RecipeMatch additive, MixAdditive type) {
			super(mixRecipe);
			this.additive = additive;
			this.type = type;
		}

		/**
		 * Gets the additive being added to the mix recipe
		 * The field RecipeMatch.amountNeeded is used to mean percent chance of the match being consumed
		 * @return  RecipeMatch additive
		 */
		public RecipeMatch getAdditive() {
			return additive;
		}

		/**
		 * Gets the additive type added to the recipe
		 * @return MixAdditive type
		 */
		public MixAdditive getType() {
			return type;
		}
	}
}
