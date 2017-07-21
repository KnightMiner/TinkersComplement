package knightminer.tcompliment.plugin.exnihilo.items;

import java.util.List;

import exnihiloadscensio.items.tools.IHammer;
import exnihiloadscensio.registries.HammerRegistry;
import knightminer.tcompliment.library.TCompRegistry;
import knightminer.tcompliment.plugin.exnihilo.ExNihiloPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

// apparently Forge does not like that this class is referenced from ExNihiloPlugin
@Optional.Interface(iface="exnihiloadscensio.items.tools.IHammer", modid="exnihiloadscensio")
public class SledgeHammer extends AoeToolCore implements IHammer {

	public SledgeHammer() {
		this(PartMaterialType.handle(TinkerTools.toolRod),
				PartMaterialType.head(ExNihiloPlugin.sledgeHead),
				PartMaterialType.extra(TinkerTools.binding));
	}

	public SledgeHammer(PartMaterialType... requiredComponents) {
		super(requiredComponents);

		setCreativeTab(TCompRegistry.tabGeneral);
		addCategory(Category.HARVEST);
		addCategory(Category.WEAPON);
	}

	@Override
	public boolean isEffective(IBlockState state) {
		return HammerRegistry.registered(state.getBlock());
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		if(isEffective(state)) {
			return calcDigSpeed(stack, state);
		}
		return super.getStrVsBlock(stack, state);
	}

	// separated to bypass canHarvest, as that checks tool type and hammers base on block type
	private static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
		if(blockState == null) {
			return 0f;
		}

		if(!stack.hasTagCompound()) {
			return 1f;
		}

		if(ToolHelper.isBroken(stack)) {
			return 0.3f;
		}

		// calculate speed depending on stats
		NBTTagCompound tag = TagUtil.getToolTag(stack);
		float speed = tag.getFloat(Tags.MININGSPEED);

		if(stack.getItem() instanceof ToolCore) {
			speed *= ((ToolCore) stack.getItem()).miningSpeedModifier();
		}

		return speed;
	}

	@Override
	public float damagePotential() {
		return 1.05f;
	}

	@Override
	public double attackSpeed() {
		return 1.15f;
	}

	@Override
	protected ToolNBT buildTagData(List<Material> materials) {
		return buildDefaultTag(materials);
	}

	/* Hammer things */

	@Optional.Method(modid="exnihiloadscensio")
	@Override
	public boolean isHammer(ItemStack stack) {
		return true;
	}

	@Override
	public int getMiningLevel(ItemStack stack) {
		return ToolHelper.getHarvestLevelStat(stack);
	}

}
