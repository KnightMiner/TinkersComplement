package knightminer.tcomplement.feature.items;

import knightminer.tcomplement.library.TCompRegistry;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemArmorBase extends ItemArmor {

	public ItemArmorBase(ArmorMaterial material, EntityEquipmentSlot slot) {
		super(material, 0, slot);
		this.setCreativeTab(TCompRegistry.tabGeneral);
	}
}
