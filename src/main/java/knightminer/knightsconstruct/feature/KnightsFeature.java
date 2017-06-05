package knightminer.knightsconstruct.feature;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.knightsconstruct.common.CommonProxy;
import knightminer.knightsconstruct.common.Config;
import knightminer.knightsconstruct.common.KnightsPulse;
import knightminer.knightsconstruct.common.ModIds;
import knightminer.knightsconstruct.feature.blocks.BlockMelter;
import knightminer.knightsconstruct.feature.tileentity.TileMelter;
import knightminer.knightsconstruct.library.KnightsRegistry;
import knightminer.knightsconstruct.shared.KnightsCommons;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockTank;

@Pulse(id = KnightsFeature.pulseID, description = "Adds standalone Knights' Construct features")
public class KnightsFeature extends KnightsPulse {
	public static final String pulseID = "KnightsFeature";

	@SidedProxy(clientSide = "knightminer.knightsconstruct.feature.FeatureClientProxy", serverSide = "knightminer.knightsconstruct.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block melter;
	public static Block porcelainMelter;
	public static BlockTank porcelainTank;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {

		// functional blocks
		melter = registerBlock(new BlockMelter(TinkerSmeltery.searedTank), "melter");

		if(isCeramicsPluginLoaded()) {
			porcelainTank = registerEnumBlock(new BlockTank(), "porcelain_tank");
			porcelainMelter = registerBlock(new BlockMelter(porcelainTank), "porcelain_melter");
		}

		registerTE(TileMelter.class, "melter");

		proxy.preInit();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		registerRecipes();
	}

	private void registerRecipes() {
		// melter recipe
		GameRegistry.addRecipe(new ItemStack(melter), " t ", "bfb", "bbb",
				't', new ItemStack(TinkerSmeltery.searedTank, 1, OreDictionary.WILDCARD_VALUE),
				'b', TinkerCommons.searedBrick,
				'f', Blocks.FURNACE);

		// porcelain tanks and melter
		if(isCeramicsPluginLoaded()) {
			ItemStack porcelainBrick = GameRegistry.makeItemStack(ModIds.Ceramics.clayUnfired, ModIds.Ceramics.porcelainMeta, 1, null);
			if(!porcelainBrick.isEmpty()) {

				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(porcelainTank, 1, BlockTank.TankType.TANK.getMeta()),
						"bbb", "bgb", "bbb", 'b', porcelainBrick, 'g', "blockGlass")); // Tank
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(porcelainTank, 1, BlockTank.TankType.GAUGE.getMeta()),
						"bgb", "ggg", "bgb", 'b', porcelainBrick, 'g', "blockGlass")); // Glass
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(porcelainTank, 1, BlockTank.TankType.WINDOW.getMeta()),
						"bgb", "bgb", "bgb", 'b', porcelainBrick, 'g', "blockGlass")); // Window

				// melter recipe
				GameRegistry.addRecipe(new ItemStack(melter), " t ", "bfb", "bbb",
						't', new ItemStack(porcelainTank, 1, OreDictionary.WILDCARD_VALUE),
						'b', porcelainBrick,
						'f', Blocks.FURNACE);
			}
		}

	}

	// POST-INITIALIZATION
	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		registerMeltingCasting();

		// add cast recipes for bucket cast
		for(FluidStack fs : TinkerSmeltery.castCreationFluids) {
			TinkerRegistry.registerTableCasting(new CastingRecipe(KnightsCommons.castBucket, new RecipeMatch.Item(new ItemStack(Items.BUCKET), 1), fs, true, true));
		}

		proxy.postInit();
	}

	private void registerMeltingCasting() {
		// cast iron buckets, because it sounds cool and opens an option for bucket gating with Ceramics
		TinkerRegistry.registerTableCasting(new ItemStack(Items.BUCKET), KnightsCommons.castBucket, TinkerFluids.iron, Material.VALUE_Ingot * 3);

		// override ore recipes to prevent ore doubling
		for(MaterialIntegration integration : TinkerRegistry.getMaterialIntegrations()) {
			if(integration.fluid != null) {
				registerOredictMeltingCasting(integration.fluid, integration.oreSuffix);
			}
		}

		// don't allow seared stone from cobblestone or stone
		KnightsRegistry.registerMelterBlacklist(RecipeMatch.of("cobblestone"));
		KnightsRegistry.registerMelterBlacklist(RecipeMatch.of("stone"));
	}

	private static void registerOredictMeltingCasting(Fluid fluid, String ore) {
		ImmutableSet.Builder<Pair<List<ItemStack>, Integer>> builder = ImmutableSet.builder();
		Pair<List<ItemStack>, Integer> oreOre = Pair.of(OreDictionary.getOres("ore" + ore), (int) (Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> oreNetherOre = Pair.of(OreDictionary.getOres("oreNether" + ore), (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> oreDenseOre = Pair.of(OreDictionary.getOres("denseore" + ore), (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> orePoorOre = Pair.of(OreDictionary.getOres("orePoor" + ore), (int) (Material.VALUE_Nugget * Config.oreToIngotRatio));

		builder.add(oreOre, oreNetherOre, oreDenseOre, orePoorOre);
		Set<Pair<List<ItemStack>, Integer>> knownOres = builder.build();

		// register oredicts
		for(Pair<List<ItemStack>, Integer> pair : knownOres) {
			KnightsRegistry.registerMelterOverride(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
		}
	}
}
