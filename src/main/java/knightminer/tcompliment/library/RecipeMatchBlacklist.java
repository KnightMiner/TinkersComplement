package knightminer.tcompliment.library;

import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.utils.ListUtil;

public class RecipeMatchBlacklist implements IBlacklist {
	private RecipeMatch match;

	public RecipeMatchBlacklist(RecipeMatch match) {
		this.match = match;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return match.matches(ListUtil.getListFrom(stack)).isPresent();
	}
}
