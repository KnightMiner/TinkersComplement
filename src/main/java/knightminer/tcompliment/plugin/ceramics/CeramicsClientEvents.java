package knightminer.tcompliment.plugin.ceramics;

import knightminer.tcompliment.library.Util;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.shared.client.BakedTableModel;

public class CeramicsClientEvents {

	// casting table/basin
	private static final String LOCATION_PorcelainCasting = Util.resource("porcelain_casting");
	private static final ModelResourceLocation locPorcelainCastingTable = new ModelResourceLocation(LOCATION_PorcelainCasting, "type=table");
	private static final ModelResourceLocation locPorcelainCastingBasin = new ModelResourceLocation(LOCATION_PorcelainCasting, "type=basin");

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		// convert casting table and basin to bakedTableModel for the item-rendering on/in them
		wrap(event, locPorcelainCastingTable);
		wrap(event, locPorcelainCastingBasin);
	}

	private static void wrap(ModelBakeEvent event, ModelResourceLocation loc) {
		IBakedModel model = event.getModelRegistry().getObject(loc);
		if(model != null && model instanceof IPerspectiveAwareModel) {
			event.getModelRegistry().putObject(loc, new BakedTableModel((IPerspectiveAwareModel) model, null, DefaultVertexFormats.ITEM));
		}
	}
}
