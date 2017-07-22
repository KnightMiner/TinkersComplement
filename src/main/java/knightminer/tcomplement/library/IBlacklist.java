package knightminer.tcomplement.library;

import net.minecraft.item.ItemStack;

public interface IBlacklist {
	public boolean matches(ItemStack stack);
}
