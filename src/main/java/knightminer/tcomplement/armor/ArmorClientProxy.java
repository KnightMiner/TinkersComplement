package knightminer.tcomplement.armor;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		// armor
		registerItemModel(ModuleArmor.manyullynHelmet);
		registerItemModel(ModuleArmor.manyullynChestplate);
		registerItemModel(ModuleArmor.manyullynLeggings);
		registerItemModel(ModuleArmor.manyullynBoots);

		registerItemModel(ModuleArmor.knightSlimeHelmet);
		registerItemModel(ModuleArmor.knightSlimeChestplate);
		registerItemModel(ModuleArmor.knightSlimeLeggings);
		registerItemModel(ModuleArmor.knightSlimeBoots);
	}
}
