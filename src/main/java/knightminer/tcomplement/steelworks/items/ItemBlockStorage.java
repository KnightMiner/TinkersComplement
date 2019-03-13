package knightminer.tcomplement.steelworks.items;

import knightminer.tcomplement.steelworks.blocks.BlockStorage.StorageType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.item.ItemBlockMeta;

public class ItemBlockStorage extends ItemBlockMeta {
	public ItemBlockStorage(Block block) {
		super(block);
	}

	@Override
	public int getItemBurnTime(ItemStack stack) {
		return stack.getMetadata() == StorageType.CHARCOAL.getMeta() ? 16000 : -1;
	}
}
