package knightminer.tcomplement.library.events;

import knightminer.tcomplement.library.IBlacklist;
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
}
