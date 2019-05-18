package knightminer.tcomplement.steelworks.blocks;

import java.util.Locale;

import javax.annotation.Nonnull;

import knightminer.tcomplement.steelworks.tileentity.TileHighOvenItemProxy.TileChute;
import knightminer.tcomplement.steelworks.tileentity.TileHighOvenItemProxy.TileDuct;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.smeltery.block.BlockEnumSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;

public class BlockHighOvenIO extends BlockEnumSmeltery<BlockHighOvenIO.IOType> {

	public static final PropertyEnum<IOType> TYPE = PropertyEnum.create("type", IOType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockHighOvenIO() {
		super(TYPE, IOType.class);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, FACING);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(TYPE, fromMeta(meta & 0b0011))
				.withProperty(FACING, EnumFacing.getHorizontal((meta & 0b1100) >> 2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		// 4 direction states -> upper 2 bit for rotation
		return state.getValue(prop).getMeta() | (state.getValue(FACING).getHorizontalIndex() << 2);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(prop).getMeta(); // no rotation in the dropped drain
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		switch(fromMeta(meta&0b0011)) {
			case DRAIN:
				return new TileDrain();
			case CHUTE:
				return new TileChute();
			case DUCT:
				return new TileDuct();
		}
		return null;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		EnumFacing side = placer.getHorizontalFacing().getOpposite();
		return this.getDefaultState().withProperty(FACING, side).withProperty(TYPE, fromMeta(meta));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// only drains need bucket filling
		if(state.getValue(TYPE) != IOType.DRAIN) {
			return false;
		}

		// we allow to insert buckets into the high oven
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(world, pos, null);
		if(fluidHandler == null) {
			return false;
		}

		ItemStack heldItem = player.getHeldItem(hand);
		IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(heldItem, fluidHandler, playerInventory, Integer.MAX_VALUE, player, true);
		if(result.isSuccess()) {
			player.setHeldItem(hand, result.getResult());
			return true;
		}

		// return true if it's a fluid handler to prevent in world interaction of the fluidhandler (bucket places liquids)
		return FluidUtil.getFluidHandler(heldItem) != null;
	}

	// at most 4
	public enum IOType implements IStringSerializable, EnumBlock.IEnumMeta {
		DRAIN,
		CHUTE,
		DUCT;

		public final int meta;

		IOType() {
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
	}
}
