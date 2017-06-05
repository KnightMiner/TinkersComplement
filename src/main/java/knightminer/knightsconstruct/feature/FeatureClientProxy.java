package knightminer.knightsconstruct.feature;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.knightsconstruct.common.ClientProxy;
import knightminer.knightsconstruct.feature.client.MelterRenderer;
import knightminer.knightsconstruct.feature.tileentity.TileMelter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import slimeknights.tconstruct.smeltery.block.BlockTank;

public class FeatureClientProxy extends ClientProxy {
	@Override
	public void registerModels() {
		registerItemModel(KnightsFeature.melter);
		registerItemModel(KnightsFeature.porcelainMelter);

		// porcelain tank items
		Item tank = Item.getItemFromBlock(KnightsFeature.porcelainTank);
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
