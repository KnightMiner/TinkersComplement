package knightminer.tcomplement.feature;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.ModIds;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.feature.blocks.BlockMelter;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.shared.ModuleCommons;
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

		// functional blocks
		melter = registerBlock(new BlockMelter(TinkerSmeltery.searedTank), "melter");
		TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(melter));

		if(isCeramicsPluginLoaded()) {
			porcelainTank = registerEnumBlock(new BlockTank(), "porcelain_tank");
			porcelainTank.setCreativeTab(TCompRegistry.tabGeneral);
			porcelainMelter = registerBlock(new BlockMelter(porcelainTank), "porcelain_melter");

		}

		registerTE(TileMelter.class, "melter", "tcompliment:melter");

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
			TinkerRegistry.registerTableCasting(new CastingRecipe(ModuleCommons.castBucket, new RecipeMatch.Item(new ItemStack(Items.BUCKET), 1), fs, true, true));
		}

		proxy.postInit();
	}

	private void registerMeltingCasting() {
		// cast iron buckets, because it sounds cool and opens an option for bucket gating with Ceramics
		TinkerRegistry.registerTableCasting(new ItemStack(Items.BUCKET), ModuleCommons.castBucket, TinkerFluids.iron, Material.VALUE_Ingot * 3);

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
		Pair<List<ItemStack>, Integer> oreOre = Pair.of(OreDictionary.getOres("ore" + ore), (int) (Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> oreNetherOre = Pair.of(OreDictionary.getOres("oreNether" + ore), (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> oreDenseOre = Pair.of(OreDictionary.getOres("denseore" + ore), (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio));
		Pair<List<ItemStack>, Integer> orePoorOre = Pair.of(OreDictionary.getOres("orePoor" + ore), (int) (Material.VALUE_Nugget * Config.oreToIngotRatio));

		builder.add(oreOre, oreNetherOre, oreDenseOre, orePoorOre);
		Set<Pair<List<ItemStack>, Integer>> knownOres = builder.build();

		// register oredicts
		for(Pair<List<ItemStack>, Integer> pair : knownOres) {
			TCompRegistry.registerMelterOverride(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
		}
	}
}
