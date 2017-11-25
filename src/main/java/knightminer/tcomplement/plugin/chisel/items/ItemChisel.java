package knightminer.tcomplement.plugin.chisel.items;

import java.util.List;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import team.chisel.api.IChiselGuiType;
import team.chisel.api.IChiselGuiType.ChiselGuiType;
import team.chisel.api.IChiselItem;
import team.chisel.api.carving.ICarvingVariation;

public class ItemChisel extends TinkerToolCore implements IChiselItem {

	public ItemChisel() {
		super(PartMaterialType.handle(TinkerTools.toolRod),
				PartMaterialType.head(ChiselPlugin.chiselHead));

		addCategory(Category.HARVEST);
		setCreativeTab(TCompRegistry.tabTools);
		addCategory(Category.WEAPON);
	}

	/* Tool stuffs */

	@Override
	public float damagePotential() {
		return 0.6f;
	}

	@Override
	public double attackSpeed() {
		return 1.15f;
	}

	@Override
	protected ToolNBT buildTagData(List<Material> materials) {
		return buildDefaultTag(materials);
	}

	/* Chisel logic */
	@Override
	public boolean canOpenGui(World world, EntityPlayer player, EnumHand hand) {
		return !ToolHelper.isBroken(player.getHeldItem(hand));
	}

	@Override
	public IChiselGuiType getGuiType(World world, EntityPlayer player, EnumHand hand) {
		return ChiselGuiType.NORMAL;
	}

	@Override
	public boolean onChisel(World world, EntityPlayer player, ItemStack chisel, ICarvingVariation target) {
		ToolHelper.damageTool(chisel, 1, player);
		return false;
	}

	@Override
	public boolean canChisel(World world, EntityPlayer player, ItemStack chisel, ICarvingVariation target) {
		// block if broken
		return !ToolHelper.isBroken(chisel);
	}

	@Override
	public boolean canChiselBlock(World world, EntityPlayer player, EnumHand hand, BlockPos pos, IBlockState state) {
		// block if broken
		ItemStack chisel = player.getHeldItem(hand);
		if(ToolHelper.isBroken(chisel)) {
			return false;
		}

		// harvest level check
		return ToolHelper.getHarvestLevelStat(chisel) >= state.getBlock().getHarvestLevel(state);
	}

	@Override
	public boolean hasModes(EntityPlayer player, EnumHand hand) {
		// TODO: supports mode
		return false;
	}

}
