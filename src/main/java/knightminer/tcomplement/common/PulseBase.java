package knightminer.tcomplement.common;

import java.util.Locale;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.melter.MelterModule;
import knightminer.tcomplement.plugin.ceramics.CeramicsPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.block.BlockStairsBase;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemBlockSlab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;

public class PulseBase {
	/* Loaded */
	protected boolean isToolsLoaded() {
		return TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId);
	}

	protected boolean isSmelteryLoaded() {
		return TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);
	}

	protected boolean isMelterLoaded() {
		return TinkersComplement.pulseManager.isPulseLoaded(MelterModule.pulseID);
	}

	protected boolean isCeramicsPluginLoaded() {
		return TinkersComplement.pulseManager.isPulseLoaded(CeramicsPlugin.pulseID);
	}

	/* Normal registration */
	protected static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Block: %s", name));
		}

		String prefixedName = Util.prefix(name);
		block.setUnlocalizedName(prefixedName);

		register(registry, block, name);
		return block;
	}

	protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockStairsBase registerBlockStairsFrom(
			IForgeRegistry<Block> registry, EnumBlock<E> block, E value, String name) {
		return registerBlock(registry, new BlockStairsBase(block.getDefaultState().withProperty(block.prop, value)),
				name);
	}

	protected static <T extends Block> T registerItemBlock(IForgeRegistry<Item> registry, T block) {

		ItemBlock itemBlock = new ItemBlockMeta(block);

		itemBlock.setUnlocalizedName(block.getUnlocalizedName());

		register(registry, itemBlock, block.getRegistryName());
		return block;
	}

	protected static <T extends EnumBlock<?>> T registerEnumItemBlock(IForgeRegistry<Item> registry, T block) {
		ItemBlock itemBlock = new ItemBlockMeta(block);

		itemBlock.setUnlocalizedName(block.getUnlocalizedName());

		register(registry, itemBlock, block.getRegistryName());
		ItemBlockMeta.setMappingProperty(block, block.prop);
		return block;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Block> T registerItemBlock(IForgeRegistry<Item> registry, ItemBlock itemBlock) {
		itemBlock.setUnlocalizedName(itemBlock.getBlock().getUnlocalizedName());

		register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
		return (T) itemBlock.getBlock();
	}

	protected static <T extends ItemBlock> T registerItemBlock(IForgeRegistry<Item> registry, T itemBlock, IProperty<?> property) {
		itemBlock.setUnlocalizedName(itemBlock.getBlock().getUnlocalizedName());

		register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
		ItemBlockMeta.setMappingProperty(itemBlock.getBlock(), property);
		return itemBlock;
	}

	protected static ItemBlockMeta registerItemBlock(IForgeRegistry<Item> registry, Block block, IProperty<?> property) {
		return registerItemBlock(registry, new ItemBlockMeta(block), property);
	}

	protected static <T extends EnumBlockSlab<?>> T registerEnumItemBlockSlab(IForgeRegistry<Item> registry, T block) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		ItemBlock itemBlock = new ItemBlockSlab(block);

		itemBlock.setUnlocalizedName(block.getUnlocalizedName());

		register(registry, itemBlock, block.getRegistryName());
		ItemBlockMeta.setMappingProperty(block, block.prop);
		return block;
	}

	/**
	 * Sets the correct unlocalized name and registers the item.
	 */
	protected static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Item: %s", name));
		}

		item.setUnlocalizedName(Util.prefix(name));
		item.setRegistryName(Util.getResource(name));
		registry.register(item);
		return item;
	}

	protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing, String name) {
		thing.setRegistryName(Util.getResource(name));
		registry.register(thing);
		return thing;
	}

	protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing,
			ResourceLocation name) {
		thing.setRegistryName(name);
		registry.register(thing);
		return thing;
	}

	protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! TE: %s", name));
		}

		GameRegistry.registerTileEntity(teClazz, Util.resource(name));
	}

	/* Tinkers Registration */
	protected static void registerStencil(ToolPart part) {
		TinkerRegistry.registerStencilTableCrafting(Pattern.setTagForPart(new ItemStack(TinkerTools.pattern), part));
	}
}
