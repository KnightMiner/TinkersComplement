package knightminer.tcomplement.shared;

import knightminer.tcomplement.common.ClientProxy;

public class CommonsClientProxy extends ClientProxy {
	@Override
	public void registerModels() {
		//KnightsCommons.materials.registerItemModels();
		ModuleCommons.castCustom.registerItemModels();
	}
}
