package knightminer.tcomplement.steelworks.inventory;

import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.BaseContainer;

public class ContainerHighOvenSideInventory extends BaseContainer<TileHighOven> {

	public final int slotCount;
	public ContainerHighOvenSideInventory(TileHighOven tile, int x, int y) {
		this(tile, null, x, y);
	}

	public ContainerHighOvenSideInventory(TileHighOven tile, EnumFacing dir, int x, int y) {
		super(tile, dir);

		// use the handler without the ingredients, makes indexing cleaner
		IItemHandler itemHandler = tile.getItemHandler();
		this.slotCount = itemHandler.getSlots();

		for (int i = 0; i < slotCount; i++) {
			this.addSlotToContainer(createSlot(itemHandler, i, x, y + i * 18));
		}
	}

	protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
		return new HighOvenSlot(itemHandler, index, x, y);
	}

	private static class HighOvenSlot extends SlotItemHandler {
		public HighOvenSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return true;
		}

		@Override
		public int getItemStackLimit(ItemStack stack) {
			return 1;
		}
	}
}
