package knightminer.tcomplement.feature;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.feature.blocks.BlockMelter;
import knightminer.tcomplement.feature.blocks.BlockMelter.MelterType;
import knightminer.tcomplement.feature.client.MelterRenderer;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockTank;

public class FeatureClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerMelterModel(ModuleFeature.melter);
		registerMelterModel(ModuleFeature.porcelainMelter);

		// porcelain tank items
		Item tank = Item.getItemFromBlock(ModuleFeature.porcelainTank);
		if(tank != null && tank != Items.AIR) {
			for(BlockTank.TankType type : BlockTank.TankType.values()) {
				String variant = String.format("%s=%s,%s=%s",
						BlockTank.KNOB.getName(),
						BlockTank.KNOB.getName(type == BlockTank.TankType.TANK),
						BlockTank.TYPE.getName(),
						BlockTank.TYPE.getName(type)
						);
				ModelLoader.setCustomModelResourceLocation(tank, type.meta, new ModelResourceLocation(tank.getRegistryName(), variant));
			}
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileMelter.class, new MelterRenderer());
	}

	private void registerMelterModel(Block block) {
		// melter items
		Item melter = Item.getItemFromBlock(block);
		if(melter != null && melter != Items.AIR) {
			for(MelterType type : BlockMelter.MelterType.values()) {
				String variant = String.format("%s=%s,%s=%s,%s=%s",
						BlockMelter.ACTIVE.getName(),
						BlockMelter.ACTIVE.getName(false),
						BlockMelter.FACING.getName(),
						BlockMelter.FACING.getName(EnumFacing.NORTH),
						BlockMelter.TYPE.getName(),
						BlockMelter.TYPE.getName(type)
						);
				ModelLoader.setCustomModelResourceLocation(melter, type.meta << 3, new ModelResourceLocation(melter.getRegistryName(), variant));
			}
		}
	}
}
