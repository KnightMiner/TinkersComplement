package knightminer.tcompliment.shared;

import knightminer.tcompliment.common.ClientProxy;

public class CommonsClientProxy extends ClientProxy {
	@Override
	public void registerModels() {
		//KnightsCommons.materials.registerItemModels();
		ModuleCommons.castCustom.registerItemModels();
	}
}
