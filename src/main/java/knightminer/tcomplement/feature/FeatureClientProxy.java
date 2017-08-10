package knightminer.tcomplement.feature;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.feature.client.MelterRenderer;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockTank;

public class FeatureClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerItemModel(ModuleFeature.melter);
		registerItemModel(ModuleFeature.porcelainMelter);

		// porcelain tank items
		Item tank = Item.getItemFromBlock(ModuleFeature.porcelainTank);
		for(BlockTank.TankType type : BlockTank.TankType.values()) {
			String variant = String.format("%s=%s,%s=%s",
					BlockTank.KNOB.getName(),
					BlockTank.KNOB.getName(type == BlockTank.TankType.TANK),
					BlockTank.TYPE.getName(),
					BlockTank.TYPE.getName(type)
					);
			ModelLoader.setCustomModelResourceLocation(tank, type.meta, new ModelResourceLocation(tank.getRegistryName(), variant));
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileMelter.class, new MelterRenderer());
	}
}
