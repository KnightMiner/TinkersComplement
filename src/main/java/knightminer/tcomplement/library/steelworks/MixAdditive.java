package knightminer.tcomplement.library.steelworks;

import java.util.Locale;

import javax.annotation.Nullable;

public enum MixAdditive {
	OXIDIZER,
	REDUCER,
	PURIFIER;

	private int slotIndex;
	MixAdditive() {
		slotIndex = this.ordinal();
	}

	/**
	 * Gets the slot index for this additive type
	 * @return  Slot index
	 */
	public int getSlotIndex() {
		return slotIndex;
	}

	/**
	 * Gets a MixAdditive from a slot index
	 * @param index  Slot index
	 * @return  MixAdditive, or null for invalid indexes
	 */
	@Nullable
	public static MixAdditive fromIndex(int index) {
		if (index < 0 || index > values().length) {
			return null;
		}
		return values()[index];
	}

	/**
	 * Gets the name in lowercase
	 * @return  name in lowercase
	 */
	public String getName() {
		return this.toString().toLowerCase(Locale.US);
	}
}
