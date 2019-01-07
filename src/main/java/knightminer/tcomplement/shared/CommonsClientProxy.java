package knightminer.tcomplement.shared;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonsClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModuleCommons.materials.registerItemModels();
		ModuleCommons.cast.registerItemModels();
		ModuleCommons.castClay.registerItemModels();
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();
		registerItemColors(colors, (stack, tintIndex) -> tintIndex == 0 ? 0xA77498 : 0xFFFFFF, ModuleCommons.castClay);
	}
}
