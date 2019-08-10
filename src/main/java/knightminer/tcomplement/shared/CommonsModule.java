package knightminer.tcomplement.shared;

import com.google.common.eventbus.Subscribe;
import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.item.CastCustom;

@Pulse(id = CommonsModule.pulseID, description = "Core feature for all the modules", forced = true)
public class CommonsModule extends PulseBase {
	public static final String pulseID = "ModuleCommons";

	@SidedProxy(clientSide = "knightminer.tcomplement.shared.CommonsClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static ItemMetaDynamic materials;
	public static ItemEdible edibles;

	public static CastCustom cast, castClay;
	public static ItemStack stoneBucket;
	public static ItemStack castBucket, castBucketClay;
	public static ItemStack scorchedBrick, steelIngot, steelNugget;

	public static ItemStack iModifier;
	public static ItemStack milkChocolateIngot, milkChocolateNugget, darkChocolateIngot, darkChocolateNugget, cocoaButter;

	public static Fluid chocolateLiquor, milkChocolate, darkChocolate;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		if(Config.general.chocolate && isSmelteryLoaded()) {
			chocolateLiquor = registerFluid(new Fluid("chocolate_liquor", FluidColored.ICON_StoneStill, FluidColored.ICON_StoneFlowing, 0xFF41220D).setTemperature(500));
			milkChocolate = registerFluid(new Fluid("milk_chocolate", FluidColored.ICON_StoneStill, FluidColored.ICON_StoneFlowing, 0xFF724428).setTemperature(400));
			if(isSteelworksLoaded()) {
				darkChocolate = registerFluid(new Fluid("dark_chocolate", FluidColored.ICON_StoneStill, FluidColored.ICON_StoneFlowing, 0xFF1E0A00).setTemperature(450));
			}
		}
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		if(Config.general.chocolate && isSmelteryLoaded()) {
			registerFluidBlock(r, chocolateLiquor);
			registerFluidBlock(r, milkChocolate);
			if(isSteelworksLoaded()) {
				registerFluidBlock(r, darkChocolate);
			}
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// materials
		materials = registerItem(r, new ItemMetaDynamic(), "materials");
		materials.setCreativeTab(TCompRegistry.tabGeneral);

		// custom casts
		if(isSmelteryLoaded()) {
			cast = registerItem(r, new CastCustom(), "cast");
			cast.setCreativeTab(TCompRegistry.tabGeneral);

			castClay = registerItem(r, new CastCustom(), "cast_clay");
			castClay.setCreativeTab(TCompRegistry.tabGeneral);

			if(Config.general.bucketCast) {
				stoneBucket = materials.addMeta(0, "stone_bucket");
				castBucket = cast.addMeta(0, "bucket", Material.VALUE_Ingot);
				castBucketClay = castClay.addMeta(0, "bucket", Material.VALUE_Ingot);
			}

			if(Config.general.chocolate) {
				edibles = registerItem(r, new ItemEdible(), "edibles");
				edibles.setCreativeTab(TCompRegistry.tabGeneral);

				milkChocolateIngot = edibles.addFood(10, 3, 0.3f, "milk_chocolate_ingot");
				milkChocolateNugget = edibles.addFood(20, 1, 0.05f, "milk_chocolate_nugget");
				cocoaButter = edibles.addFood(30, 2, 0.2f, "cocoa_butter", new PotionEffect(MobEffects.HUNGER, 20 * 10));
				if(isSteelworksLoaded()) {
					darkChocolateIngot = edibles.addFood(11, 4, 0.4f, "dark_chocolate_ingot");
					darkChocolateNugget = edibles.addFood(21, 1, 0.1f, "dark_chocolate_nugget");
				}

				FluidRegistry.addBucketForFluid(chocolateLiquor);
				FluidRegistry.addBucketForFluid(milkChocolate);
				if(isSteelworksLoaded()) {
					FluidRegistry.addBucketForFluid(darkChocolate);
				}
			}
		}

		if(isSteelworksLoaded()) {
			scorchedBrick = materials.addMeta(1, "scorched_brick");
			// ingots start at 10
			steelIngot = materials.addMeta(10, "steel_ingot");
			// nuggets start at 20
			steelNugget = materials.addMeta(20, "steel_nugget");
		}

		if (isChiselPluginLoaded()) {
			iModifier = materials.addMeta(6, "imodifier");
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
		if(isSmelteryLoaded()) {
			if(Config.general.bucketCast) {
				// cast iron buckets, because it sounds cool and opens an option for bucket gating with Ceramics
				TinkerRegistry.registerTableCasting(new ItemStack(Items.BUCKET), castBucket, TinkerFluids.iron, Material.VALUE_Ingot * 3);
				// add cast recipes for bucket cast
				for(FluidStack fs : TinkerSmeltery.castCreationFluids) {
					TinkerRegistry.registerTableCasting(new CastingRecipe(castBucket, new RecipeMatch.Item(stoneBucket, 1), fs, true, true));
				}

				// use clay cast to make iron buckets, so you don't need gold
				TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Items.BUCKET), RecipeMatch.of(castBucketClay), TinkerFluids.iron, Material.VALUE_Ingot * 3, true, false));
				// add cast recipes for bucket cast
				for(FluidStack fs : TinkerSmeltery.clayCreationFluids) {
					TinkerRegistry.registerTableCasting(new CastingRecipe(castBucketClay, new RecipeMatch.Item(stoneBucket, 1), fs, true, true));
				}
			}

			// chocolate
			if(Config.general.chocolate) {
				// cast chocolate and melt it back
				TinkerRegistry.registerTableCasting(new CastingRecipe(milkChocolateIngot.copy(), RecipeMatch.of(TinkerSmeltery.castIngot), milkChocolate, Material.VALUE_Ingot, 60));
				TinkerRegistry.registerTableCasting(new CastingRecipe(milkChocolateNugget.copy(), RecipeMatch.of(TinkerSmeltery.castNugget), milkChocolate, Material.VALUE_Nugget, 20));
				TinkerRegistry.registerMelting(milkChocolateIngot, milkChocolate, Material.VALUE_Ingot);
				TinkerRegistry.registerMelting(milkChocolateNugget, milkChocolate, Material.VALUE_Nugget);
				if(isSteelworksLoaded()) {
					TinkerRegistry.registerTableCasting(new CastingRecipe(darkChocolateIngot.copy(), RecipeMatch.of(TinkerSmeltery.castIngot), darkChocolate, Material.VALUE_Ingot, 80));
					TinkerRegistry.registerTableCasting(new CastingRecipe(darkChocolateNugget.copy(), RecipeMatch.of(TinkerSmeltery.castNugget), darkChocolate, Material.VALUE_Nugget, 30));
					TinkerRegistry.registerMelting(darkChocolateIngot, darkChocolate, Material.VALUE_Ingot);
					TinkerRegistry.registerMelting(darkChocolateNugget, darkChocolate, Material.VALUE_Nugget);
				}
				// cocoa butter for making dark chocolate
				TinkerRegistry.registerTableCasting(new CastingRecipe(cocoaButter.copy(), RecipeMatch.of(TinkerSmeltery.castIngot), chocolateLiquor, Material.VALUE_Ingot, 80));

				// make chocolate liquor
				ItemStack cocoaBeans = new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage());
				TinkerRegistry.registerMelting(cocoaBeans, chocolateLiquor, Material.VALUE_Ore() / 3);
				if(isMelterLoaded()) {
					TCompRegistry.registerMelterOverride(new MeltingRecipe(RecipeMatch.of(cocoaBeans, (int)(Material.VALUE_Ingot * Config.melter.oreToIngotRatio / 3)), chocolateLiquor));
				}
				if(isMelterLoaded()) {
					TCompRegistry.registerHighOvenOverride(new MeltingRecipe(RecipeMatch.of(cocoaBeans, (int)(Material.VALUE_Ingot * Config.highOven.oreToIngotRatio / 3)), chocolateLiquor));
				}

				// milk chocolate
				TinkerRegistry.registerAlloy(new FluidStack(milkChocolate, Material.VALUE_Nugget), new FluidStack(chocolateLiquor, Material.VALUE_Nugget / 2), new FluidStack(TinkerFluids.milk, 20));

				// dark chocolate recipe handled in steelworks module
			}
		}
	}
}
