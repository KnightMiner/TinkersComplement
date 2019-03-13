package knightminer.tcomplement.steelworks;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.steelworks.blocks.BlockHighOvenController;
import knightminer.tcomplement.steelworks.blocks.BlockStorage;
import knightminer.tcomplement.steelworks.blocks.BlockStorage.StorageType;
import knightminer.tcomplement.steelworks.items.ItemBlockStorage;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
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
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;

@Pulse(id = SteelworksModule.pulseID, description = "Adds the high oven: a new multiblock for making steel")
public class SteelworksModule extends PulseBase {
	public static final String pulseID = "ModuleSteelworks";

	@SidedProxy(clientSide = "knightminer.tcomplement.steelworks.SteelworksClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static BlockHighOvenController highOvenController;
	public static Block storage;

	public static ItemStack charcoalBlock, steelBlock;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		storage = registerBlock(r, new BlockStorage(), "storage");

		if(isSmelteryLoaded()) {
			highOvenController = registerBlock(r, new BlockHighOvenController(), "high_oven_controller");
			registerTE(TileHighOven.class, "hign_oven");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		registerItemBlock(r, new ItemBlockStorage(storage), BlockStorage.TYPE);
		charcoalBlock = new ItemStack(storage, 1, StorageType.CHARCOAL.getMeta());
		steelBlock = new ItemStack(storage, 1, StorageType.STEEL.getMeta());

		if(isSmelteryLoaded()) {
			registerItemBlock(r, highOvenController);
			if(!isMelterLoaded()) {
				TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(highOvenController));
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
		registerMixes();
		registerFuels();

		proxy.postInit();
	}

	private void registerMixes() {
		@SuppressWarnings("unused")
		IMixRecipe mix; // because Eclipse formatter is dumb

		// steel
		mix = TCompRegistry.registerMix(new FluidStack(TinkerFluids.steel, Material.VALUE_Ingot),
				new FluidStack(TinkerFluids.iron, Material.VALUE_Ingot))
				// oxidizers
				.addOxidizer("gunpowder", 33)
				.addOxidizer("dustSulfur", 29)
				.addOxidizer("dustSulphur", 29)
				.addOxidizer("dustSaltpeter", 30)
				// reducers
				.addReducer("dustRedstone", 65)
				.addReducer("dustManganese", 47)
				.addReducer("dustAluminum", 60)
				.addReducer("dustAluminium", 60)
				// purifiers
				.addPurifier("sand", 100);

		// pig iron
		mix = TCompRegistry.registerMix(new FluidStack(TinkerFluids.pigIron, Material.VALUE_Ingot),
				new FluidStack(TinkerFluids.iron, Material.VALUE_Ingot))
				.addOxidizer(new ItemStack(Items.SUGAR), 60)
				.addReducer(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), 20)
				.addPurifier(new ItemStack(Items.PORKCHOP), 80);

		// knightslime
		mix = TCompRegistry.registerMix(new FluidStack(TinkerFluids.knightslime, Material.VALUE_Ingot/2),
				new FluidStack(TinkerFluids.iron, Material.VALUE_Ingot/2))
				.addReducer("slimeballPurple", 75)
				// reducers
				.addPurifier("gravel", 80);
	}

	private void registerFuels() {
		// only pure fuels allowed
		TCompRegistry.registerFuel(new ItemStack(Items.COAL, 1, 1), 140, 4);
		TCompRegistry.registerFuel("blockCharcoal", 1400, 7);
		TCompRegistry.registerFuel("fuelCoke", 280, 10);
		TCompRegistry.registerFuel("blockFuelCoke", 2800, 15);
	}

	private void registerMeltingCasting() {
		// override ore recipes to prevent ore doubling
		for(MaterialIntegration integration : TinkerRegistry.getMaterialIntegrations()) {
			if(integration.fluid != null) {
				registerOredictMeltingCasting(integration.fluid, integration.oreSuffix);
			}
		}
	}

	private static void registerOredictMeltingCasting(Fluid fluid, String ore) {
		ImmutableSet.Builder<Pair<String, Integer>> builder = ImmutableSet.builder();
		builder.add(Pair.of("ore" + ore, (int) (Material.VALUE_Ingot * Config.highOven.oreToIngotRatio)));
		builder.add(Pair.of("oreNether" + ore, (int) (2 * Material.VALUE_Ingot * Config.highOven.oreToIngotRatio)));
		builder.add(Pair.of("denseore" + ore, (int) (3 * Material.VALUE_Ingot * Config.highOven.oreToIngotRatio)));
		builder.add(Pair.of("orePoor" + ore, (int) (Material.VALUE_Nugget * 3 * Config.highOven.oreToIngotRatio)));
		builder.add(Pair.of("oreNugget" + ore, (int) (Material.VALUE_Nugget * Config.highOven.oreToIngotRatio)));

		Set<Pair<String, Integer>> knownOres = builder.build();

		// register oredicts
		for(Pair<String, Integer> pair : knownOres) {
			TCompRegistry.registerHighOvenOverride(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
		}
	}
}
