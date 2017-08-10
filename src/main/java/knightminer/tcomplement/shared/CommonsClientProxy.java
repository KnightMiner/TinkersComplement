package knightminer.tcomplement.shared;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonsClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModuleCommons.materials.registerItemModels();
		ModuleCommons.castCustom.registerItemModels();
	}
}
