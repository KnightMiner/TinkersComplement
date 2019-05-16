package knightminer.tcomplement.shared;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.client.book.repository.ModuleFileRepository;
import slimeknights.tconstruct.library.book.TinkerBook;

public class CommonsClientProxy extends ClientProxy {
	@Override
	public void preInit() {
		super.preInit();
		TinkerBook.INSTANCE.addRepository(new ModuleFileRepository(TinkersComplement.pulseManager, Util.resource("book")));
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		CommonsModule.materials.registerItemModels();
		registerItemModelDynamic(CommonsModule.cast);
		registerItemModelDynamic(CommonsModule.castClay);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();
		registerItemColors(colors, (stack, tintIndex) -> tintIndex == 0 ? 0xA77498 : 0xFFFFFF, CommonsModule.castClay);
	}
}
