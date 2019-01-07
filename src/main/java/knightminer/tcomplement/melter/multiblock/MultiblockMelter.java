package knightminer.tcomplement.melter.multiblock;

import com.google.common.collect.ImmutableList;

import knightminer.tcomplement.melter.blocks.BlockMelter;
import knightminer.tcomplement.melter.blocks.BlockMelter.MelterType;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;

public class MultiblockMelter extends MultiblockDetection {

	private TileMelter tile;
	private Block block;

	public MultiblockMelter(TileMelter tile) {
		this.tile = tile;
	}

	// quick and dirty multiblock
	@Override
	public MultiblockStructure detectMultiblock(World world, BlockPos center, int limit) {
		// multiblock code starts by offsetting inwards, so work from the tile entity instead

		BlockPos pos = tile.getPos();
		block = world.getBlockState(pos).getBlock();
		BlockPos below = pos.down();
		if(!isValidBlock(world, below)) {
			return null;
		}

		return new MultiblockStructure(1, 2, 1, ImmutableList.<BlockPos>of(pos, below));
	}

	@Override
	public boolean isValidBlock(World world, BlockPos pos) {
		if(!isValidSlave(world, pos)) {
			return false;
		}

		// safety check, make sure the block is a melter and ask it what block it needs for a tank
		if(block instanceof BlockMelter) {
			// allow if the block is the specified tank or our block's heater
			IBlockState state = world.getBlockState(pos);
			Block tank = state.getBlock();
			return tank == ((BlockMelter)block).getMelterTank()
					|| (tank == block && state.getValue(BlockMelter.TYPE) == MelterType.HEATER);
		}

		return false;
	}

	// cloned from MultiblockTinker as I don't need most of the cuboid logic
	protected boolean isValidSlave(World world, BlockPos pos) {
		if(!world.isBlockLoaded(pos)) {
			return false;
		}
		TileEntity te = world.getTileEntity(pos);

		// slave-blocks are only allowed if they already belong to this structure
		if(te instanceof IServantLogic) {
			IServantLogic slave = (IServantLogic) te;
			BlockPos masterPos = slave.getMasterPosition();
			if(masterPos != null) {
				if(!tile.getPos().equals(masterPos)) {
					return false;
				}
			}
		}

		return true;
	}

	// probably still overkill, but the sqrt check returned false for my tiny multiblock
	@Override
	public boolean checkIfMultiblockCanBeRechecked(World world, MultiblockStructure structure) {
		return structure != null && world.isAreaLoaded(structure.minPos, structure.maxPos);
	}

}
