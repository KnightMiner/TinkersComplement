package knightminer.tcomplement.feature.inventory;

import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHeaterFuel extends SlotItemHandler {
	public SlotHeaterFuel(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return TileMelter.isFuelValid(stack);
	}
}
