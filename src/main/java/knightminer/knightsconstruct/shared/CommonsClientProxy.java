package knightminer.knightsconstruct.shared;

import knightminer.knightsconstruct.common.ClientProxy;

public class CommonsClientProxy extends ClientProxy {
	@Override
	public void registerModels() {
		//KnightsCommons.materials.registerItemModels();
		KnightsCommons.castCustom.registerItemModels();
	}
}
