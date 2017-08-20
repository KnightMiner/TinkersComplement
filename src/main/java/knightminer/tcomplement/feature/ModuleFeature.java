package knightminer.tcomplement.feature;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.feature.blocks.BlockMelter;
import knightminer.tcomplement.feature.tileentity.TileHeater;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.shared.ModuleCommons;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
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
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = ModuleFeature.pulseID, description = "Adds standalone Knights' Construct features")
public class ModuleFeature extends PulseBase {
	public static final String pulseID = "ModuleFeature";

	@SidedProxy(clientSide = "knightminer.tcomplement.feature.FeatureClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block melter;
	public static Block porcelainMelter;
	public static BlockTank porcelainTank;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		// functional blocks
		melter = registerBlock(r, new BlockMelter(TinkerSmeltery.searedTank), "melter");

		if(isCeramicsPluginLoaded()) {
			porcelainTank = registerBlock(r, new BlockTank(), "porcelain_tank");
			porcelainTank.setCreativeTab(TCompRegistry.tabGeneral);
			porcelainMelter = registerBlock(r, new BlockMelter(porcelainTank), "porcelain_melter");

		}

		registerTE(TileMelter.class, "melter");
		registerTE(TileHeater.class, "heater");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		/* itemblocks */
		registerItemBlock(r, melter, BlockMelter.TYPE);
		TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(melter));

		if(isCeramicsPluginLoaded()) {
			registerEnumItemBlock(r, porcelainTank);
			registerItemBlock(r, porcelainMelter, BlockMelter.TYPE);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	// POST-INITIALIZATION
	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
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

		// override ore recipes to prevent ore doubling
		for(MaterialIntegration integration : TinkerRegistry.getMaterialIntegrations()) {
			if(integration.fluid != null) {
				registerOredictMeltingCasting(integration.fluid, integration.oreSuffix);
			}
		}

		// don't allow seared stone from cobblestone or stone
		if(Config.blacklistMelterStone) {
			TCompRegistry.registerMelterBlacklist(RecipeMatch.of("cobblestone"));
			TCompRegistry.registerMelterBlacklist(RecipeMatch.of("stone"));
			TCompRegistry.registerMelterBlacklist(new PartMaterialBlacklist(TinkerMaterials.stone));
		}
	}

	private static void registerOredictMeltingCasting(Fluid fluid, String ore) {
		ImmutableSet.Builder<Pair<List<ItemStack>, Integer>> builder = ImmutableSet.builder();
		builder.add(Pair.of(OreDictionary.getOres("ore" + ore), (int) (Material.VALUE_Ingot * Config.oreToIngotRatio)));
		builder.add(Pair.of(OreDictionary.getOres("oreNether" + ore), (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio)));
		builder.add(Pair.of(OreDictionary.getOres("denseore" + ore), (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio)));
		builder.add(Pair.of(OreDictionary.getOres("orePoor" + ore), (int) (Material.VALUE_Nugget * 3 * Config.oreToIngotRatio)));
		builder.add(Pair.of(OreDictionary.getOres("oreNugget" + ore), (int) (Material.VALUE_Nugget * Config.oreToIngotRatio)));

		Set<Pair<List<ItemStack>, Integer>> knownOres = builder.build();

		// register oredicts
		for(Pair<List<ItemStack>, Integer> pair : knownOres) {
			TCompRegistry.registerMelterOverride(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
		}
	}
}
