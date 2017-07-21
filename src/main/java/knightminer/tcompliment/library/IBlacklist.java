package knightminer.tcompliment.library;

import net.minecraft.item.ItemStack;

public interface IBlacklist {
	public boolean matches(ItemStack stack);
}
