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
import knightminer.tcomplement.shared.CommonsModule;
import knightminer.tcomplement.steelworks.blocks.BlockHighOvenController;
import knightminer.tcomplement.steelworks.blocks.BlockHighOvenIO;
import knightminer.tcomplement.steelworks.blocks.BlockScorchedSlab;
import knightminer.tcomplement.steelworks.blocks.BlockScorchedSlab2;
import knightminer.tcomplement.steelworks.blocks.BlockStorage;
import knightminer.tcomplement.steelworks.blocks.BlockStorage.StorageType;
import knightminer.tcomplement.steelworks.items.ItemBlockStorage;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import knightminer.tcomplement.steelworks.tileentity.TileHighOvenItemProxy.TileChute;
import knightminer.tcomplement.steelworks.tileentity.TileHighOvenItemProxy.TileDuct;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockChannel;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSeared.SearedType;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab2;
import slimeknights.tconstruct.smeltery.item.ItemChannel;

@Pulse(id = SteelworksModule.pulseID, description = "Adds the high oven: a new multiblock for making steel")
public class SteelworksModule extends PulseBase {
	public static final String pulseID = "ModuleSteelworks";

	@SidedProxy(clientSide = "knightminer.tcomplement.steelworks.SteelworksClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	// Blocks
	public static Block storage;
	// High Oven Blocks
	public static BlockHighOvenController highOvenController;
	public static BlockFaucet scorchedFaucet;
	public static BlockChannel scorchedChannel;
	public static BlockCasting scorchedCasting;
	public static BlockHighOvenIO highOvenIO;
	// Scorched
	public static BlockSeared scorchedBlock;
	public static BlockScorchedSlab scorchedSlab;
	public static BlockScorchedSlab2 scorchedSlab2;
	// Scorched stairs
	public static Block scorchedStairsStone;
	public static Block scorchedStairsCobble;
	public static Block scorchedStairsPaver;
	public static Block scorchedStairsBrick;
	public static Block scorchedStairsBrickCracked;
	public static Block scorchedStairsBrickFancy;
	public static Block scorchedStairsBrickSquare;
	public static Block scorchedStairsBrickTriangle;
	public static Block scorchedStairsBrickSmall;
	public static Block scorchedStairsRoad;
	public static Block scorchedStairsTile;
	public static Block scorchedStairsCreeper;

	public static ItemStack charcoalBlock, steelBlock;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		storage = registerBlock(r, new BlockStorage(), "storage");

		// High Oven blocks
		highOvenController = registerBlock(r, new BlockHighOvenController(), "high_oven_controller");
		scorchedFaucet = registerBlock(r, new BlockFaucet(), "scorched_faucet");
		scorchedChannel = registerBlock(r, new BlockChannel(), "scorched_channel");
		scorchedCasting = registerBlock(r, new BlockCasting(), "scorched_casting");
		highOvenIO = registerBlock(r, new BlockHighOvenIO(), "high_oven_io");

		scorchedFaucet.setCreativeTab(TCompRegistry.tabGeneral);
		scorchedChannel.setCreativeTab(TCompRegistry.tabGeneral);
		scorchedCasting.setCreativeTab(TCompRegistry.tabGeneral);
		highOvenIO.setCreativeTab(TCompRegistry.tabGeneral);
		registerTE(TileHighOven.class, "high_oven");
		registerTE(TileChute.class, "chute");
		registerTE(TileDuct.class, "duct");

		// Scorched
		scorchedBlock = registerBlock(r, new BlockSeared(),        "scorched_block");
		scorchedSlab =  registerBlock(r, new BlockScorchedSlab(),  "scorched_slab");
		scorchedSlab2 = registerBlock(r, new BlockScorchedSlab2(), "scorched_slab2");
		scorchedBlock.setCreativeTab(TCompRegistry.tabGeneral);

		// stairs
		scorchedStairsStone         = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.STONE,          "scorched_stairs_stone");
		scorchedStairsCobble        = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.COBBLE,         "scorched_stairs_cobble");
		scorchedStairsPaver         = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.PAVER,          "scorched_stairs_paver");
		scorchedStairsBrick         = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK,          "scorched_stairs_brick");
		scorchedStairsBrickCracked  = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK_CRACKED,  "scorched_stairs_brick_cracked");
		scorchedStairsBrickFancy    = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK_FANCY,    "scorched_stairs_brick_fancy");
		scorchedStairsBrickSquare   = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK_SQUARE,   "scorched_stairs_brick_square");
		scorchedStairsBrickTriangle = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK_TRIANGLE, "scorched_stairs_brick_triangle");
		scorchedStairsBrickSmall    = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.BRICK_SMALL,    "scorched_stairs_brick_small");
		scorchedStairsRoad          = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.ROAD,           "scorched_stairs_road");
		scorchedStairsTile          = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.TILE,           "scorched_stairs_tile");
		scorchedStairsCreeper       = registerBlockStairsFrom(r, scorchedBlock, BlockSeared.SearedType.CREEPER,        "scorched_stairs_creeper");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		registerItemBlock(r, new ItemBlockStorage(storage), BlockStorage.TYPE);
		charcoalBlock = new ItemStack(storage, 1, StorageType.CHARCOAL.getMeta());
		steelBlock = new ItemStack(storage, 1, StorageType.STEEL.getMeta());

		// High Oven
		registerItemBlock(r, highOvenController);
		registerItemBlock(r, scorchedFaucet);
		registerItemBlock(r, new ItemChannel(scorchedChannel));
		registerItemBlock(r, new ItemBlockMeta(scorchedCasting), BlockCasting.TYPE);
		registerEnumItemBlock(r, highOvenIO);

		if(!isMelterLoaded()) {
			TCompRegistry.tabGeneral.setDisplayIcon(new ItemStack(highOvenController));
		}

		// Scorched
		registerEnumItemBlock(r, scorchedBlock);
		registerEnumItemBlockSlab(r, scorchedSlab);
		registerEnumItemBlockSlab(r, scorchedSlab2);
		// Scorched stairs
		registerItemBlock(r, scorchedStairsStone);
		registerItemBlock(r, scorchedStairsCobble);
		registerItemBlock(r, scorchedStairsPaver);
		registerItemBlock(r, scorchedStairsBrick);
		registerItemBlock(r, scorchedStairsBrickCracked);
		registerItemBlock(r, scorchedStairsBrickFancy);
		registerItemBlock(r, scorchedStairsBrickSquare);
		registerItemBlock(r, scorchedStairsBrickTriangle);
		registerItemBlock(r, scorchedStairsBrickSmall);
		registerItemBlock(r, scorchedStairsRoad);
		registerItemBlock(r, scorchedStairsTile);
		registerItemBlock(r, scorchedStairsCreeper);
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
		mix = TCompRegistry.registerMix(new FluidStack(TinkerFluids.steel, (int)(Material.VALUE_Ingot*Config.highOven.oreToIngotRatio)),
				new FluidStack(TinkerFluids.iron, (int)(Material.VALUE_Ingot*Config.highOven.oreToIngotRatio)))
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
		// bricks
		TinkerRegistry.registerTableCasting(new CastingRecipe(CommonsModule.scorchedBrick.copy(),
				RecipeMatch.of(Items.BRICK), new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial/4), 40, true, false));
		// raw clay -> cobble
		TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(scorchedBlock, 1, SearedType.COBBLE.getMeta()),
				RecipeMatch.of(Blocks.CLAY), new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial), 80, true, false));
		// terracotta -> stone
		TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(scorchedBlock, 1, SearedType.STONE.getMeta()),
				RecipeMatch.of(Blocks.HARDENED_CLAY), new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial), 100, true, false));
		TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(scorchedBlock, 1, SearedType.STONE.getMeta()),
				RecipeMatch.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE)),
				new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial), 100, true, false));
		// brick block
		TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(scorchedBlock, 1, SearedType.BRICK_SMALL.getMeta()),
				RecipeMatch.of(Blocks.BRICK_BLOCK), new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial), 100, true, false));
		// brick slab
		TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(scorchedSlab2, 1, BlockSearedSlab2.SearedType.BRICK_SMALL.getMeta()),
				RecipeMatch.of(new ItemStack(Blocks.STONE_SLAB, 1, BlockStoneSlab.EnumType.BRICK.getMetadata())),
				new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial/2), 60, true, false));

		// turn cobble into stone
		GameRegistry.addSmelting(new ItemStack(scorchedBlock, 1, SearedType.COBBLE.getMeta()),
				new ItemStack(scorchedBlock, 1, SearedType.STONE.getMeta()), 0.5f);

		// make casts from the brick
		// honestly, no idea why you would do this, but felt natural to include
		for(FluidStack fluid : TinkerSmeltery.castCreationFluids) {
			TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerSmeltery.castIngot.copy(), RecipeMatch.of(CommonsModule.scorchedBrick), fluid, true, false));
		}

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
