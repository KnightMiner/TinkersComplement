package knightminer.tcomplement.plugin.chisel;

import knightminer.tcomplement.common.ClientProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

public class ChiselPluginClientProxy extends ClientProxy {
	@SubscribeEvent
	protected void registerModels(ModelRegistryEvent event) {
		ModelRegisterUtil.registerPartModel(ChiselPlugin.chiselHead);
		ModelRegisterUtil.registerToolModel(ChiselPlugin.chisel);
	}

	@Override
	public void init() {
		// chisel
		ToolBuildGuiInfo info = new ToolBuildGuiInfo(ChiselPlugin.chisel);
		info.addSlotPosition(33 - 21, 42 + 13); // rod
		info.addSlotPosition(33 + 9, 42 - 15); // chisel head
		TinkerRegistryClient.addToolBuilding(info);
	}
}
