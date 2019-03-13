package knightminer.tcomplement.steelworks.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.block.BlockMultiblockController;

public class BlockHighOvenController extends BlockMultiblockController {

	public BlockHighOvenController() {
		super(Material.ROCK);
		this.setCreativeTab(TCompRegistry.tabGeneral);
		this.setHardness(3F);
		this.setResistance(20F);
		this.setSoundType(SoundType.METAL);
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileHighOven();
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(isActive(world, pos)) {
			EnumFacing enumfacing = state.getValue(FACING);
			double d0 = pos.getX() + 0.5D;
			double d1 = pos.getY() + 0.5D + (rand.nextFloat() * 6F) / 16F;
			double d2 = pos.getZ() + 0.5D;
			double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			spawnFireParticles(world, enumfacing, d0, d1, d2, d3, d4);
		}
	}

}
