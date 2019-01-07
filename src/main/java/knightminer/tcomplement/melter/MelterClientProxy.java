package knightminer.tcomplement.melter;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import knightminer.tcomplement.common.ClientProxy;
import knightminer.tcomplement.melter.blocks.BlockAlloyTank;
import knightminer.tcomplement.melter.blocks.BlockMelter;
import knightminer.tcomplement.melter.blocks.BlockMelter.MelterType;
import knightminer.tcomplement.melter.client.MelterRenderer;
import knightminer.tcomplement.melter.tileentity.TileAlloyTank;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.client.TankItemModel;
import slimeknights.tconstruct.smeltery.client.TankRenderer;

public class MelterClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		StateMap alloyTankMap = new StateMap.Builder().ignore(BlockAlloyTank.POWERED).build();
		setModelStateMapper(ModuleMelter.alloyTank, alloyTankMap);
		setModelStateMapper(ModuleMelter.porcelainAlloyTank, alloyTankMap);

		registerMelterModel(ModuleMelter.melter);
		registerItemModel(ModuleMelter.alloyTank);
		registerMelterModel(ModuleMelter.porcelainMelter);
		registerItemModel(ModuleMelter.porcelainAlloyTank);

		// porcelain tank items
		Item tank = Item.getItemFromBlock(ModuleMelter.porcelainTank);
		if(tank != null && tank != Items.AIR) {
			for(BlockTank.TankType type : BlockTank.TankType.values()) {
				ModelLoader.setCustomModelResourceLocation(tank, type.meta, new ModelResourceLocation(tank.getRegistryName(), type.getName()));
			}
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileMelter.class, new MelterRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAlloyTank.class, new TankRenderer());
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();
		registerItemColors(colors, (stack, tintIndex) -> {
			if(!stack.hasTagCompound()) {
				return 0xFFFFFF;
			}
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
			if (fluid != null && fluid.amount > 0 && fluid.getFluid() != null) {
				return fluid.getFluid().getColor(fluid);
			}
			return 0xFFFFFF;
		}, ModuleMelter.porcelainTank, ModuleMelter.alloyTank, ModuleMelter.porcelainAlloyTank);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for (BlockTank.TankType type : BlockTank.TankType.values()) {
			replaceTankModel(event, ModuleMelter.porcelainTank, type.getName());
		}
		replaceTankModel(event, ModuleMelter.alloyTank, "inventory");
		replaceTankModel(event, ModuleMelter.porcelainAlloyTank, "inventory");
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

	private void replaceTankModel(ModelBakeEvent event, Block block, String variant) {
		if (block == null) {
			return;
		}
		ModelResourceLocation loc = new ModelResourceLocation(block.getRegistryName(), variant);
		IBakedModel baked = event.getModelRegistry().getObject(loc);
		if(baked != null) {
			event.getModelRegistry().putObject(loc, new TankItemModel(baked));
		}
	}
}
