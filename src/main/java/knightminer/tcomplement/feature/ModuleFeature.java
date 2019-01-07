package knightminer.tcomplement.feature;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.ModIds;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.feature.blocks.BlockAlloyTank;
import knightminer.tcomplement.feature.blocks.BlockMelter;
import knightminer.tcomplement.feature.items.ItemArmorBase;
import knightminer.tcomplement.feature.items.ItemKnightSlimeArmor;
import knightminer.tcomplement.feature.tileentity.TileAlloyTank;
import knightminer.tcomplement.feature.tileentity.TileHeater;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.shared.ModuleCommons;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.item.ItemTank;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = ModuleFeature.pulseID, description = "Adds standalone Knights' Construct features")
public class ModuleFeature extends PulseBase {
	public static final String pulseID = "ModuleFeature";

	@SidedProxy(clientSide = "knightminer.tcomplement.feature.FeatureClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block melter;
	public static Block alloyTank;
	public static Block porcelainMelter;
	public static Block porcelainAlloyTank;
	public static BlockTank porcelainTank;

	// armor
	public static ArmorMaterial manyullynArmor;
	public static Item manyullynHelmet;
	public static Item manyullynChestplate;
	public static Item manyullynLeggings;
	public static Item manyullynBoots;

	public static ArmorMaterial knightSlimeArmor;
	public static Item knightSlimeHelmet;
	public static Item knightSlimeChestplate;
	public static Item knightSlimeLeggings;
	public static Item knightSlimeBoots;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		manyullynArmor = EnumHelper.addArmorMaterial(Util.prefix("manyullyn"), Util.resource("manyullyn"),
				15, new int[]{3, 6, 8, 3}, 7, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0F);
		knightSlimeArmor = EnumHelper.addArmorMaterial(Util.prefix("knightslime"), Util.resource("knightslime"),
				12, new int[]{2, 5, 6, 2}, 4, SoundEvents.BLOCK_SLIME_PLACE, 1.0F);

		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		// functional blocks
		if(isSmelteryLoaded()) {
			melter = registerBlock(r, new BlockMelter(TinkerSmeltery.searedTank), "melter");
			alloyTank = registerBlock(r, new BlockAlloyTank(melter, TinkerSmeltery.searedTank), "alloy_tank");

			registerTE(TileMelter.class, "melter");
			registerTE(TileHeater.class, "heater");
			registerTE(TileAlloyTank.class, "alloy_tank");
		}

		if(isCeramicsPluginLoaded()) {
			porcelainTank = registerBlock(r, new BlockTank(), "porcelain_tank");
			porcelainTank.setCreativeTab(TCompRegistry.tabGeneral);
			if(isSmelteryLoaded()) {
				porcelainMelter = registerBlock(r, new BlockMelter(porcelainTank), "porcelain_melter");
				porcelainAlloyTank = registerBlock(r, new BlockAlloyTank(porcelainMelter, porcelainTank), "porcelain_alloy_tank");
			}
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// armor
		manyullynHelmet = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.HEAD), "manyullyn_helmet");
		manyullynChestplate = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.CHEST), "manyullyn_chestplate");
		manyullynLeggings = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.LEGS), "manyullyn_leggings");
		manyullynBoots = registerItem(r, new ItemArmorBase(manyullynArmor, EntityEquipmentSlot.FEET), "manyullyn_boots");
		ItemStack manyullyn = GameRegistry.makeItemStack(ModIds.TConstruct.ingots, ModIds.TConstruct.manyullynMeta, 1, null);
		if(!manyullyn.isEmpty()) {
			manyullynArmor.setRepairItem(manyullyn);
		}

		// knight slime armor
		knightSlimeHelmet = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.HEAD), "knightslime_helmet");
		knightSlimeChestplate = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.CHEST), "knightslime_chestplate");
		knightSlimeLeggings = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.LEGS), "knightslime_leggings");
		knightSlimeBoots = registerItem(r, new ItemKnightSlimeArmor(EntityEquipmentSlot.FEET), "knightslime_boots");
		ItemStack knightSlime = GameRegistry.makeItemStack(ModIds.TConstruct.ingots, ModIds.TConstruct.knightSlimeMeta, 1, null);
		if(!knightSlime.isEmpty()) {
			knightSlimeArmor.setRepairItem(knightSlime);
		}

		// itemblocks
		if(isSmelteryLoaded()) {
			registerItemBlock(r, melter, BlockMelter.TYPE);
			registerItemBlock(r, new ItemTank(alloyTank));
			TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(melter));
		}

		if(isCeramicsPluginLoaded()) {
			registerItemBlock(r, new ItemTank(porcelainTank), BlockTank.TYPE);
			if(isSmelteryLoaded()) {
				registerItemBlock(r, porcelainMelter, BlockMelter.TYPE);
				registerItemBlock(r, new ItemTank(porcelainAlloyTank));
			}
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	// POST-INITIALIZATION
	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ItemKnightSlimeArmor.class);

		registerMeltingCasting();

		proxy.postInit();
	}

	private void registerMeltingCasting() {
		// cast iron buckets, because it sounds cool and opens an option for bucket gating with Ceramics
		TinkerRegistry.registerTableCasting(new ItemStack(Items.BUCKET), ModuleCommons.castBucket, TinkerFluids.iron, Material.VALUE_Ingot * 3);
		// add cast recipes for bucket cast
		for(FluidStack fs : TinkerSmeltery.castCreationFluids) {
			TinkerRegistry.registerTableCasting(new CastingRecipe(ModuleCommons.castBucket, new RecipeMatch.Item(ModuleCommons.stoneBucket, 1), fs, true, true));
		}

		// use clay cast to make iron buckets, so you don't need gold
		if (ModuleCommons.castBucketClay != null) {
			TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Items.BUCKET), RecipeMatch.of(ModuleCommons.castBucketClay), TinkerFluids.iron, Material.VALUE_Ingot * 3, true, false));
			// add cast recipes for bucket cast
			for(FluidStack fs : TinkerSmeltery.clayCreationFluids) {
				TinkerRegistry.registerTableCasting(new CastingRecipe(ModuleCommons.castBucketClay, new RecipeMatch.Item(ModuleCommons.stoneBucket, 1), fs, true, true));
			}
		}

		// override ore recipes to prevent ore doubling
		for(MaterialIntegration integration : TinkerRegistry.getMaterialIntegrations()) {
			if(integration.fluid != null) {
				registerOredictMeltingCasting(integration.fluid, integration.oreSuffix);
			}
		}

		// don't allow seared stone from cobblestone or stone
		if(Config.melter.blacklistStone) {
			TCompRegistry.registerMelterBlacklist(RecipeMatch.of("cobblestone"));
			TCompRegistry.registerMelterBlacklist(RecipeMatch.of("stone"));
			TCompRegistry.registerMelterBlacklist(new PartMaterialBlacklist(TinkerMaterials.stone));
		}
	}

	private static void registerOredictMeltingCasting(Fluid fluid, String ore) {
		ImmutableSet.Builder<Pair<String, Integer>> builder = ImmutableSet.builder();
		builder.add(Pair.of("ore" + ore, (int) (Material.VALUE_Ingot * Config.melter.oreToIngotRatio)));
		builder.add(Pair.of("oreNether" + ore, (int) (2 * Material.VALUE_Ingot * Config.melter.oreToIngotRatio)));
		builder.add(Pair.of("denseore" + ore, (int) (3 * Material.VALUE_Ingot * Config.melter.oreToIngotRatio)));
		builder.add(Pair.of("orePoor" + ore, (int) (Material.VALUE_Nugget * 3 * Config.melter.oreToIngotRatio)));
		builder.add(Pair.of("oreNugget" + ore, (int) (Material.VALUE_Nugget * Config.melter.oreToIngotRatio)));

		Set<Pair<String, Integer>> knownOres = builder.build();

		// register oredicts
		for(Pair<String, Integer> pair : knownOres) {
			TCompRegistry.registerMelterOverride(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
		}
	}
}
