package knightminer.knightsconstruct.plugin.ceramics;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;

import knightminer.knightsconstruct.common.ClientProxy;
import net.minecraftforge.common.MinecraftForge;

public class CeramicsPluginClientProxy extends ClientProxy {

	@Override
	protected void registerModels() {
		registerItemBlockMeta(CeramicsPlugin.porcelainCasting);
	}

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(CeramicsClientEvents.class);
	}
}
