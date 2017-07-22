package knightminer.tcomplement.common;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.ModuleFeature;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.plugin.ceramics.CeramicsPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemBlockSlab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockMolten;
import slimeknights.tconstruct.tools.TinkerTools;

public class PulseBase {

	protected boolean isToolsLoaded() {
		return TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId);
	}

	protected boolean isSmelteryLoaded() {
		return TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);
	}

	protected boolean isFeaturesLoaded() {
		return TinkersComplement.pulseManager.isPulseLoaded(ModuleFeature.pulseID);
	}

	protected boolean isCeramicsPluginLoaded() {
		return TinkersComplement.pulseManager.isPulseLoaded(CeramicsPlugin.pulseID);
	}

	/* Blocks */

	protected static <T extends Block> T registerBlock(T block, String name) {
		ItemBlock itemBlock = new ItemBlockMeta(block);
		registerBlock(block, itemBlock, name);
		return block;
	}

	protected static <T extends EnumBlock<?>> T registerEnumBlock(T block, String name) {
		registerBlock(block, new ItemBlockMeta(block), name);
		ItemBlockMeta.setMappingProperty(block, block.prop);
		return block;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected static <T extends EnumBlockSlab<?>> T registerEnumBlockSlab(T block, String name) {
		registerBlock(block, new ItemBlockSlab(block), name);
		ItemBlockMeta.setMappingProperty(block, block.prop);
		return block;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Block> T registerBlock(ItemBlock itemBlock, String name) {
		Block block = itemBlock.getBlock();
		return (T) registerBlock(block, itemBlock, name);
	}

	protected static <T extends Block> T registerBlock(T block, String name, IProperty<?> property) {
		ItemBlockMeta itemBlock = new ItemBlockMeta(block);
		registerBlock(block, itemBlock, name);
		ItemBlockMeta.setMappingProperty(block, property);
		return block;
	}

	protected static <T extends Block> T registerBlock(T block, ItemBlock itemBlock, String name) {
		String prefixedName = Util.prefix(name);
		block.setUnlocalizedName(prefixedName);
		itemBlock.setUnlocalizedName(prefixedName);

		register(block, name);
		register(itemBlock, name);
		return block;
	}

	protected static <T extends Block> T registerBlockNoItem(T block, String name) {
		String prefixedName = Util.prefix(name);
		block.setUnlocalizedName(prefixedName);

		register(block, name);
		return block;
	}


	/* Items */

	protected static <T extends Item> T registerItem(T item, String name) {
		item.setUnlocalizedName(Util.prefix(name));
		register(item, name);

		return item;
	}

	// Tinkers Items

	protected static <T extends ToolCore> T registerTool(T tool, String name) {
		T ret = registerItem(tool, name);

		return ret;
	}

	protected static <T extends ToolPart> T registerToolPart(T part, String name) {
		T item = registerItem(part, name);
		registerStencil(part);

		return item;
	}

	protected static void registerStencil(ToolPart part) {
		ItemStack stencil = new ItemStack(TinkerTools.pattern);
		Pattern.setTagForPart(stencil, part);
		TinkerRegistry.registerStencilTableCrafting(stencil);
	}

	/* Misc */
	public static BlockMolten registerMoltenBlock(Fluid fluid) {
		BlockMolten block = new BlockMolten(fluid);
		return registerBlockNoItem(block, "molten_" + fluid.getName()); // molten_foobar prefix
	}

	protected static <T extends Fluid> T registerFluid(T fluid) {
		fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
		FluidRegistry.registerFluid(fluid);

		return fluid;
	}

	protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
		GameRegistry.registerTileEntity(teClazz, Util.resource(name));
	}

	protected static void registerTE(Class<? extends TileEntity> teClazz, String name, String... alts) {
		GameRegistry.registerTileEntityWithAlternatives(teClazz, Util.resource(name), alts);
	}

	protected static <T extends IForgeRegistryEntry<?>> T register(T thing, String name) {
		thing.setRegistryName(Util.getResource(name));
		GameRegistry.register(thing);
		return thing;
	}

	/* recipes */

	// sets the stack size to make Knights Commons easier, as it uses base itemstacks there
	protected static void addSlabRecipe(ItemStack slab, ItemStack input) {
		GameRegistry.addShapedRecipe(new ItemStack(slab.getItem(), 6, slab.getItemDamage()), "BBB", 'B', input);
	}

	protected static void addBrickRecipe(Block block, EnumBlock.IEnumMeta out, EnumBlock.IEnumMeta in) {
		ItemStack brickBlockIn = new ItemStack(block, 1, in.getMeta());
		ItemStack brickBlockOut = new ItemStack(block, 1, out.getMeta());

		// todo: convert to chisel recipes if chisel is present
		//GameRegistry.addShapedRecipe(searedBrickBlockOut, "BB", "BB", 'B', searedBrickBlockIn);
		GameRegistry.addShapelessRecipe(brickBlockOut, brickBlockIn);
	}
}
