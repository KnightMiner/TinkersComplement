package knightminer.tcomplement.steelworks.blocks;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.steelworks.SteelworksModule;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab.SearedType;

public class BlockScorchedSlab extends EnumBlockSlab<SearedType> {

	public BlockScorchedSlab() {
		super(Material.ROCK, BlockSearedSlab.TYPE, SearedType.class);
		this.setCreativeTab(TCompRegistry.tabGeneral);
		this.setHardness(3F);
		this.setResistance(20F);
		this.setSoundType(SoundType.METAL);
	}

	@Override
	public IBlockState getFullBlock(IBlockState state) {
		if(SteelworksModule.scorchedBlock == null) {
			return null;
		}
		return SteelworksModule.scorchedBlock.getDefaultState().withProperty(BlockSeared.TYPE, state.getValue(BlockSearedSlab.TYPE).asSearedBlock());
	}
}
