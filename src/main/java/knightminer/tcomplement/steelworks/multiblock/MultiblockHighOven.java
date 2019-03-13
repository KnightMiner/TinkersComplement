package knightminer.tcomplement.steelworks.multiblock;

import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockTinker;

public class MultiblockHighOven extends MultiblockTinker {

	public MultiblockHighOven(TileHighOven tile) {
		super(tile, true, true, true);
	}

	@Override
	public MultiblockStructure detectMultiblock(World world, BlockPos center, int limit) {
		// hardcode size limit of 1 as that is not easily changed in TileMultiblock
		return super.detectMultiblock(world, center, 1);
	}

	@Override
	public boolean isValidBlock(World world, BlockPos pos) {
		// controller always is valid
		if(pos.equals(tile.getPos())) {
			return true;
		}

		// seared blocks or drains
		Block block = world.getBlockState(pos).getBlock();
		return (block == TinkerSmeltery.searedBlock || block == TinkerSmeltery.smelteryIO) && isValidSlave(world, pos);
	}
}
