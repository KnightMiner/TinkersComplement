package knightminer.tcomplement.feature.items;

import knightminer.tcomplement.library.TCompRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorBase extends ItemArmor {

	private ResourceLocation texture;
	public ItemArmorBase(ArmorMaterial material, EntityEquipmentSlot slot) {
		super(material, 0, slot);
		this.setCreativeTab(TCompRegistry.tabGeneral);
		this.texture = new ResourceLocation(material.getName());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		if(slot == EntityEquipmentSlot.LEGS) {
			return String.format("%s:textures/models/armor/%s_leggings.png", texture.getResourceDomain(), texture.getResourcePath());
		}

		return String.format("%s:textures/models/armor/%s.png", texture.getResourceDomain(), texture.getResourcePath());
	}

}
