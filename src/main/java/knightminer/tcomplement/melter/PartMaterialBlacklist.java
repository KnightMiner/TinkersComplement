package knightminer.tcomplement.melter;

import knightminer.tcomplement.library.IBlacklist;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;

public class PartMaterialBlacklist implements IBlacklist {

	private Material material;
	public PartMaterialBlacklist(Material material) {
		this.material = material;
	}
	@Override
	public boolean matches(ItemStack stack) {
		if(stack.isEmpty() || !(stack.getItem() instanceof IToolPart)) {
			return false;
		}

		return ((IToolPart)stack.getItem()).getMaterial(stack) == this.material;
	}
}
