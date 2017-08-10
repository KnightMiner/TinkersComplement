package knightminer.tcomplement.plugin.ceramics;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.shared.client.BakedTableModel;

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
		wrap(event, locPorcelainCastingTable);
		wrap(event, locPorcelainCastingBasin);
	}

	private static void wrap(ModelBakeEvent event, ModelResourceLocation loc) {
		IBakedModel model = event.getModelRegistry().getObject(loc);
		if(model != null) {
			event.getModelRegistry().putObject(loc, new BakedTableModel(model, null, DefaultVertexFormats.ITEM));
		}
	}
}
