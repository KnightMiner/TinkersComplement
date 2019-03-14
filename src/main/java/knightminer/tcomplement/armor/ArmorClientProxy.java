package knightminer.tcomplement.armor;

import static knightminer.tcomplement.armor.ArmorModule.knightSlimeBoots;
import static knightminer.tcomplement.armor.ArmorModule.knightSlimeChestplate;
import static knightminer.tcomplement.armor.ArmorModule.knightSlimeHelmet;
import static knightminer.tcomplement.armor.ArmorModule.knightSlimeLeggings;
import static knightminer.tcomplement.armor.ArmorModule.manyullynBoots;
import static knightminer.tcomplement.armor.ArmorModule.manyullynChestplate;
import static knightminer.tcomplement.armor.ArmorModule.manyullynHelmet;
import static knightminer.tcomplement.armor.ArmorModule.manyullynLeggings;
import static knightminer.tcomplement.armor.ArmorModule.steelBoots;
import static knightminer.tcomplement.armor.ArmorModule.steelChestplate;
import static knightminer.tcomplement.armor.ArmorModule.steelHelmet;
import static knightminer.tcomplement.armor.ArmorModule.steelLeggings;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		// manyullyn
		registerItemModel(manyullynHelmet);
		registerItemModel(manyullynChestplate);
		registerItemModel(manyullynLeggings);
		registerItemModel(manyullynBoots);

		// knightslime
		registerItemModel(knightSlimeHelmet);
		registerItemModel(knightSlimeChestplate);
		registerItemModel(knightSlimeLeggings);
		registerItemModel(knightSlimeBoots);

		// steel
		registerItemModel(steelHelmet);
		registerItemModel(steelChestplate);
		registerItemModel(steelLeggings);
		registerItemModel(steelBoots);
	}
}
