package knightminer.tcomplement.armor;

import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.armor.items.ItemArmorBase;
import knightminer.tcomplement.armor.items.ItemKnightSlimeArmor;
import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.ModIds;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.Util;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = ArmorModule.pulseID, description = "Adds KnightSlime and Manyullyn vanilla style armors")
public class ArmorModule extends PulseBase {
	public static final String pulseID = "ModuleArmor";

	@SidedProxy(clientSide = "knightminer.tcomplement.armor.ArmorClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	// armor
	public static ArmorMaterial manyullynArmor;
	public static Item manyullynHelmet;
	public static Item manyullynChestplate;
	public static Item manyullynLeggings;
	public static Item manyullynBoots;

	public static ArmorMaterial knightSlimeArmor;
	public static Item knightSlimeHelmet;
	public static Item knightSlimeChestplate;
	public static Item knightSlimeLeggings;
	public static Item knightSlimeBoots;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		manyullynArmor = EnumHelper.addArmorMaterial(Util.prefix("manyullyn"), Util.resource("manyullyn"),
				15, new int[]{3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0F);
		knightSlimeArmor = EnumHelper.addArmorMaterial(Util.prefix("knightslime"), Util.resource("knightslime"),
				12, new int[]{2, 5, 6, 2}, 4, SoundEvents.BLOCK_SLIME_PLACE, 1.0F);

		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// manyullyn armor
		manyullynHelmet = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.HEAD), "manyullyn_helmet");
		manyullynChestplate = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.CHEST), "manyullyn_chestplate");
		manyullynLeggings = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.LEGS), "manyullyn_leggings");
		manyullynBoots = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.FEET), "manyullyn_boots");
		ItemStack manyullyn = GameRegistry.makeItemStack(ModIds.TConstruct.ingots, ModIds.TConstruct.manyullynMeta, 1, null);
		if(!manyullyn.isEmpty()) {
			manyullynArmor.setRepairItem(manyullyn);
		}

		// knight slime armor
		knightSlimeHelmet = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.HEAD), "knightslime_helmet");
		knightSlimeChestplate = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.CHEST), "knightslime_chestplate");
		knightSlimeLeggings = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.LEGS), "knightslime_leggings");
		knightSlimeBoots = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.FEET), "knightslime_boots");
		ItemStack knightSlime = GameRegistry.makeItemStack(ModIds.TConstruct.ingots, ModIds.TConstruct.knightSlimeMeta, 1, null);
		if(!knightSlime.isEmpty()) {
			knightSlimeArmor.setRepairItem(knightSlime);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ItemKnightSlimeArmor.class);

		proxy.postInit();
	}
}
