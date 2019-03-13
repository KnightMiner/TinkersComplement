package knightminer.tcomplement.steelworks;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SteelworksClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerItemBlockMeta(SteelworksModule.storage);
		registerItemModel(SteelworksModule.highOvenController);
	}
}
