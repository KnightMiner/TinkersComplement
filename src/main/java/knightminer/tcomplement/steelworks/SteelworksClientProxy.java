package knightminer.tcomplement.steelworks;

import static knightminer.tcomplement.steelworks.SteelworksModule.highOvenController;
import static knightminer.tcomplement.steelworks.SteelworksModule.highOvenIO;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedBlock;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedCasting;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedChannel;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedFaucet;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedSlab;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedSlab2;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrick;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrickCracked;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrickFancy;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrickSmall;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrickSquare;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsBrickTriangle;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsCobble;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsCreeper;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsPaver;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsRoad;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsStone;
import static knightminer.tcomplement.steelworks.SteelworksModule.scorchedStairsTile;
import static knightminer.tcomplement.steelworks.SteelworksModule.storage;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;

public class SteelworksClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerItemBlockMeta(storage);

		// High Oven Blocks
		registerItemModel(highOvenController);
		registerItemModel(scorchedFaucet);
		registerItemModel(scorchedChannel);
		registerItemBlockMeta(scorchedCasting);

		// Scorched
		registerItemBlockMeta(scorchedBlock);
		registerItemBlockMeta(scorchedSlab);
		registerItemBlockMeta(scorchedSlab2);
		// Scorched Stairs
		registerItemModel(scorchedStairsStone);
		registerItemModel(scorchedStairsCobble);
		registerItemModel(scorchedStairsPaver);
		registerItemModel(scorchedStairsBrick);
		registerItemModel(scorchedStairsBrickCracked);
		registerItemModel(scorchedStairsBrickFancy);
		registerItemModel(scorchedStairsBrickSquare);
		registerItemModel(scorchedStairsBrickTriangle);
		registerItemModel(scorchedStairsBrickSmall);
		registerItemModel(scorchedStairsRoad);
		registerItemModel(scorchedStairsTile);
		registerItemModel(scorchedStairsCreeper);

		// High Oven IO
		// TODO: ducts
		Item io = Item.getItemFromBlock(highOvenIO);
		for(BlockSmelteryIO.IOType type : BlockSmelteryIO.IOType.values()) {
			String variant = String.format("facing=south,type=%s", type.getName());
			ModelLoader.setCustomModelResourceLocation(io, type.meta, new ModelResourceLocation(io.getRegistryName(), variant));
		}
	}

	private static final ResourceLocation SCORCHED_CASTING = Util.getResource("scorched_casting");
	private static final ModelResourceLocation SCORCHED_CASTING_TABLE = new ModelResourceLocation(SCORCHED_CASTING, "type=table");
	private static final ModelResourceLocation SCORCHED_CASTING_BASIN = new ModelResourceLocation(SCORCHED_CASTING, "type=basin");

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		// convert casting table and basin to bakedTableModel for the item-rendering on/in them
		wrapTableModel(event, SCORCHED_CASTING_TABLE);
		wrapTableModel(event, SCORCHED_CASTING_BASIN);
	}
}
