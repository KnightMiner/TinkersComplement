package knightminer.tcomplement.plugin.ceramics;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CeramicsPluginClientProxy extends ClientProxy {

	@SubscribeEvent
	protected void registerModels(ModelRegistryEvent event) {
		registerItemBlockMeta(CeramicsPlugin.porcelainCasting);
	}

	/* Table models */

	// casting table/basin
	private static final String LOCATION_PorcelainCasting = Util.resource("porcelain_casting");
	private static final ModelResourceLocation locPorcelainCastingTable = new ModelResourceLocation(LOCATION_PorcelainCasting, "type=table");
	private static final ModelResourceLocation locPorcelainCastingBasin = new ModelResourceLocation(LOCATION_PorcelainCasting, "type=basin");

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		// convert casting table and basin to bakedTableModel for the item-rendering on/in them
		wrapTableModel(event, locPorcelainCastingTable);
		wrapTableModel(event, locPorcelainCastingBasin);
	}
}
