package knightminer.tcompliment.plugin.exnihilo;

import knightminer.tcompliment.common.ClientProxy;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

public class ENPluginClientProxy extends ClientProxy {
	@Override
	public void registerModels() {
		ModelRegisterUtil.registerPartModel(ExNihiloPlugin.sledgeHead);
		ModelRegisterUtil.registerToolModel(ExNihiloPlugin.sledgeHammer);
	}

	@Override
	public void init() {
		// sledge hammer
		// yeah, basically the same locations as the pickaxe
		ToolBuildGuiInfo info;

		info = new ToolBuildGuiInfo(ExNihiloPlugin.sledgeHammer);
		info.addSlotPosition(33 - 18, 42 + 18); // rod
		info.addSlotPosition(33 + 20, 42 - 20); // sledge head
		info.addSlotPosition(33, 42); // binding
		TinkerRegistryClient.addToolBuilding(info);
	}
}
