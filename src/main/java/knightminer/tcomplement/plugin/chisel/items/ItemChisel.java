package knightminer.tcomplement.plugin.chisel.items;

import java.util.List;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import team.chisel.api.IChiselGuiType;
import team.chisel.api.IChiselGuiType.ChiselGuiType;
import team.chisel.api.IChiselItem;
import team.chisel.api.carving.ICarvingVariation;
import team.chisel.api.carving.IChiselMode;

@Optional.Interface(iface="team.chisel.api.IChiselItem", modid="chisel")
public class ItemChisel extends AoeToolCore implements IChiselItem {

	public static final float DURABILITY_MODIFIER = 2.25f;

	public ItemChisel() {
		super(PartMaterialType.handle(TinkerTools.toolRod),
				PartMaterialType.head(ChiselPlugin.chiselHead));

		setCreativeTab(TCompRegistry.tabTools);
		addCategory(Category.WEAPON);
	}

	/* Tool stuffs */

	@Override
	public float damagePotential() {
		return 0.5f;
	}

	@Override
	public double attackSpeed() {
		return 1.15f;
	}

	@Override
	public float getRepairModifierForPart(int index) {
		return DURABILITY_MODIFIER;
	}

	@Override
	public ToolNBT buildTagData(List<Material> materials) {
		HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
		HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);

		ToolNBT data = new ToolNBT();
		data.head(head);
		data.handle(handle);

		data.harvestLevel = head.harvestLevel;
		data.durability *= DURABILITY_MODIFIER;

		return data;
	}

	/* Chisel logic */
	@Optional.Method(modid="chisel")
	@Override
	public boolean canOpenGui(World world, EntityPlayer player, EnumHand hand) {
		return !ToolHelper.isBroken(player.getHeldItem(hand));
	}

	@Optional.Method(modid="chisel")
	@Override
	public IChiselGuiType getGuiType(World world, EntityPlayer player, EnumHand hand) {
		return ChiselGuiType.NORMAL;
	}

	@Optional.Method(modid="chisel")
	@Override
	public boolean supportsMode(EntityPlayer player, ItemStack stack, IChiselMode mode) {
		String name = mode.name();
		if(name.equals("SINGLE")) {
			return true;
		}

		// use expanders to determine ability
		NBTTagCompound tags = TagUtil.getTagSafe(stack);
		boolean hasWidth = TinkerUtil.hasModifier(tags, TinkerModifiers.modHarvestWidth.getIdentifier());
		boolean hasHeight = TinkerUtil.hasModifier(tags, TinkerModifiers.modHarvestHeight.getIdentifier());

		switch(name) {
			case "COLUMN":
				return hasHeight;
			case "ROW":
				return hasWidth;
			case "PANEL":
				return hasWidth && hasHeight;
		}

		return false;
	}

	@Optional.Method(modid="chisel")
	@Override
	public boolean onChisel(World world, EntityPlayer player, ItemStack chisel, ICarvingVariation target) {
		return true;
	}

	@Optional.Method(modid="chisel")
	@Override
	public boolean canChisel(World world, EntityPlayer player, ItemStack chisel, ICarvingVariation target) {
		// block if broken
		return !ToolHelper.isBroken(chisel);
	}

	@Optional.Method(modid="chisel")
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

	@Optional.Method(modid="chisel")
	@Override
	public ItemStack craftItem(ItemStack chisel, ItemStack source, ItemStack target, EntityPlayer player) {
		int toCraft = Math.min(source.getCount(), target.getMaxStackSize());
		int damageLeft = chisel.getMaxDamage() - chisel.getItemDamage();
		toCraft = Math.min(toCraft, damageLeft);
		ToolHelper.damageTool(chisel, toCraft, player);

		ItemStack res = target.copy();
		source.shrink(toCraft);
		res.setCount(toCraft);
		return res;
	}

}
