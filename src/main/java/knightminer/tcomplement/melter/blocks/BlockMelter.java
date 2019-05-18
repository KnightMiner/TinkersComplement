package knightminer.tcomplement.melter.blocks;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.melter.tileentity.TileHeater;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.smeltery.block.BlockMultiblockController;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.tileentity.TileMultiblock;

public class BlockMelter extends BlockMultiblockController {
	public final static PropertyEnum<MelterType> TYPE = PropertyEnum.create("type", MelterType.class);

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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float clickX, float clickY, float clickZ) {
		if(state.getValue(TYPE) == MelterType.MELTER && Util.onFluidTankActivated(world, pos, player, hand, facing)) {
			return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, facing, clickX, clickY, clickZ);
	}

	/* Multiblock logic */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(this.getStateFromMeta(meta).getValue(TYPE) == MelterType.HEATER) {
			return new TileHeater();
		}
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
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// check structure
		TileMultiblock<?> te = getTile(worldIn, pos);
		if(te != null) {
			te.checkMultiblockStructure();
		}
	}

	@Override
	protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		// if the block is a melter, only allow the GUI if active
		if(world.getBlockState(pos).getValue(TYPE) == MelterType.MELTER && !isActive(world, pos)) {
			return false;
		}

		player.openGui(TinkersComplement.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	/* Heater logic */

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, FACING, ACTIVE);
	}

	@Nonnull
	@Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		// need to override to allow any TE instead of just multiblock
		if(world.getTileEntity(pos) != null) {
			return state.withProperty(ACTIVE, isActive(world, pos));
		}
		return state;
	}

	@Override
	public boolean isActive(IBlockAccess world, BlockPos pos) {
		// if a heater, the block above must be a heater consumer
		if(world.getBlockState(pos).getValue(TYPE) == MelterType.HEATER) {
			TileEntity te = world.getTileEntity(pos.up());
			return te instanceof IHeaterConsumer;
		}

		// otherwise use the default for the controller
		return super.isActive(world, pos);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		// set rotation
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta & 7);
		// why?? you could have just used getHorizontal
		if(enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		MelterType type = MelterType.fromMeta(meta >> 3);

		return this.getDefaultState().withProperty(TYPE, type).withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex()
				+ (state.getValue(TYPE).getMeta() << 3);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE).getMeta() << 3;
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		// melter
		items.add(new ItemStack(this, 1, 0));
		// heater
		items.add(new ItemStack(this, 1, 8));
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

	// at most 2
	public enum MelterType implements IStringSerializable, EnumBlock.IEnumMeta {
		MELTER,
		HEATER;

		public final int meta;

		MelterType() {
			meta = ordinal();
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}

		@Override
		public int getMeta() {
			return meta;
		}

		public static MelterType fromMeta(int meta) {
			if(meta < 0 || meta >= values().length) {
				meta = 0;
			}

			return values()[meta];
		}
	}
}
