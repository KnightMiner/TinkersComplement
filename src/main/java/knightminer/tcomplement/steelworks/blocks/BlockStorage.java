package knightminer.tcomplement.steelworks.blocks;

import java.util.Locale;

import javax.annotation.Nullable;

import knightminer.tcomplement.library.TCompRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;

public class BlockStorage extends EnumBlock<BlockStorage.StorageType> {

	public static final PropertyEnum<StorageType> TYPE = PropertyEnum.create("type", StorageType.class);
	public BlockStorage() {
		super(Material.IRON, TYPE, StorageType.class);

		setHardness(5f);
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(TCompRegistry.tabGeneral);
		for(StorageType type : StorageType.values()) {
			setHarvestLevel("pickaxe", type.getHarvestLevel(), this.getDefaultState().withProperty(TYPE, type));
		}
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == this && state.getValue(TYPE) == StorageType.CHARCOAL;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == this && state.getValue(TYPE) == StorageType.STEEL;
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		if(state.getValue(TYPE) == StorageType.CHARCOAL) {
			return SoundType.STONE;
		}
		return SoundType.METAL;
	}

	public enum StorageType implements IStringSerializable, EnumBlock.IEnumMeta {
		CHARCOAL(0),
		STEEL(2);

		private final int meta, harvestLevel;
		StorageType(int harvestLevel) {
			this.meta = ordinal();
			this.harvestLevel = harvestLevel;
		}

		public int getHarvestLevel() {
			return harvestLevel;
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