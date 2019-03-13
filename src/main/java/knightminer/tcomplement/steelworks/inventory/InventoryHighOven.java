package knightminer.tcomplement.steelworks.inventory;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryHighOven extends ItemStackHandler {

	public static final int SLOT_FUEL = 3;

	private TileHighOven tile;
	public InventoryHighOven(TileHighOven tile) {
		super(4); // constant size
		this.tile = tile;
	}

	/**
	 * Gets the current stack in the fuel slot
	 * @return  Current fuel stack
	 */
	public ItemStack getFuel() {
		return getStackInSlot(SLOT_FUEL);
	}

	/**
	 * Gets the stack cooresponding to a given additive
	 * @param type  Additive to fetch stack for
	 * @return  ItemStack additive
	 */
	public ItemStack getAdditive(@Nonnull MixAdditive type) {
		return getStackInSlot(type.getSlotIndex());
	}

	public boolean itemValidForSlot(int slot, @Nonnull ItemStack stack) {
		if (slot == SLOT_FUEL) {
			return TCompRegistry.isHighOvenFuel(stack);
		}

		MixAdditive type = MixAdditive.fromIndex(slot);
		if (type != null) {
			return TCompRegistry.isValidMixAdditive(stack, type);
		}
		// invalid slot
		return false;
	}

	// little bit of a hack, we return 0 to signify the slot is not valid
	@Override
	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		if(!itemValidForSlot(slot, stack)) {
			return 0;
		}
		return super.getStackLimit(slot, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirtyFast();
	}
}
