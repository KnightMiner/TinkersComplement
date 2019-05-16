package knightminer.tcomplement.melter;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.melter.blocks.BlockAlloyTank;
import knightminer.tcomplement.melter.blocks.BlockMelter;
import knightminer.tcomplement.melter.tileentity.TileAlloyTank;
import knightminer.tcomplement.melter.tileentity.TileHeater;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.item.ItemTank;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(
		id = MelterModule.pulseID,
		description = "Adds the melter and alloyer: smaller components of the smeltery",
		pulsesRequired = "tconstruct:TinkerSmeltery"
		)
public class MelterModule extends PulseBase {
	public static final String pulseID = "ModuleMelter";

	@SidedProxy(clientSide = "knightminer.tcomplement.melter.MelterClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static BlockMelter melter;
	public static Block alloyTank;
	public static BlockMelter porcelainMelter;
	public static Block porcelainAlloyTank;
	public static BlockTank porcelainTank;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
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

			if(isCeramicsPluginLoaded()) {
				porcelainTank = registerBlock(r, new BlockTank(), "porcelain_tank");
				porcelainTank.setCreativeTab(TCompRegistry.tabGeneral);
				porcelainMelter = registerBlock(r, new BlockMelter(porcelainTank), "porcelain_melter");
				porcelainAlloyTank = registerBlock(r, new BlockAlloyTank(porcelainMelter, porcelainTank), "porcelain_alloy_tank");
			}
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		// itemblocks
		if(isSmelteryLoaded()) {
			registerItemBlock(r, melter, BlockMelter.TYPE);
			registerItemBlock(r, new ItemTank(alloyTank));
			TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(melter));

			if(isCeramicsPluginLoaded()) {
				registerItemBlock(r, new ItemTank(porcelainTank), BlockTank.TYPE);
				registerItemBlock(r, porcelainMelter, BlockMelter.TYPE);
				registerItemBlock(r, new ItemTank(porcelainAlloyTank));
			}
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		registerMeltingCasting();

		proxy.postInit();
	}

	private void registerMeltingCasting() {
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
