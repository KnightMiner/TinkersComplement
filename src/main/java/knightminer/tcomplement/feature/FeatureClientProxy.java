package knightminer.tcomplement.feature;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.feature.blocks.BlockAlloyTank;
import knightminer.tcomplement.feature.blocks.BlockMelter;
import knightminer.tcomplement.feature.blocks.BlockMelter.MelterType;
import knightminer.tcomplement.feature.client.MelterRenderer;
import knightminer.tcomplement.feature.tileentity.TileAlloyTank;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.client.TankRenderer;

public class FeatureClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		StateMap alloyTankMap = new StateMap.Builder().ignore(BlockAlloyTank.POWERED).build();
		setModelStateMapper(ModuleFeature.alloyTank, alloyTankMap);
		setModelStateMapper(ModuleFeature.porcelainAlloyTank, alloyTankMap);

		registerMelterModel(ModuleFeature.melter);
		registerItemModel(ModuleFeature.alloyTank);
		registerMelterModel(ModuleFeature.porcelainMelter);
		registerItemModel(ModuleFeature.porcelainAlloyTank);

		// armor
		registerItemModel(ModuleFeature.manyullynHelmet);
		registerItemModel(ModuleFeature.manyullynChestplate);
		registerItemModel(ModuleFeature.manyullynLeggings);
		registerItemModel(ModuleFeature.manyullynBoots);

		registerItemModel(ModuleFeature.knightSlimeHelmet);
		registerItemModel(ModuleFeature.knightSlimeChestplate);
		registerItemModel(ModuleFeature.knightSlimeLeggings);
		registerItemModel(ModuleFeature.knightSlimeBoots);

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlloyTank.class, new TankRenderer());
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
