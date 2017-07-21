package knightminer.tcompliment.feature.blocks;

import java.util.Random;

import knightminer.tcompliment.TinkersCompliment;
import knightminer.tcompliment.feature.tileentity.TileMelter;
import knightminer.tcompliment.library.TCompRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.smeltery.block.BlockMultiblockController;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.tileentity.TileMultiblock;

public class BlockMelter extends BlockMultiblockController {

	private BlockTank melterTank;

	public BlockMelter(BlockTank melterTank) {
		super(Material.ROCK);
		this.setCreativeTab(TCompRegistry.tabGeneral);
		this.setHardness(3F);
		this.setResistance(20F);
		this.setSoundType(SoundType.METAL);

		this.melterTank = melterTank;
	}

	public BlockTank getMelterTank() {
		return melterTank;
	}

	/* Multiblock logic */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMelter();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// check structure
		TileMultiblock<?> te = getTile(worldIn, pos);
		if(te != null) {
			te.checkMultiblockStructure();
		}
	}

	@Override
	protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		if(!isActive(world, pos)) {
			return false;
		}

		player.openGui(TinkersCompliment.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	/* Rendering */

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(isActive(world, pos)) {
			EnumFacing enumfacing = state.getValue(FACING);
			double x = pos.getX() + 0.5D;
			double y = (double) pos.getY() + (rand.nextFloat() * 6F) / 16F;
			double z = pos.getZ() + 0.5D;
			double frontOffset = 0.52D;
			double sideOffset = rand.nextDouble() * 0.6D - 0.3D;

			spawnFireParticles(world, enumfacing, x, y, z, frontOffset, sideOffset);
		}
	}
}
