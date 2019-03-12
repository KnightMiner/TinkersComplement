package knightminer.tcomplement.shared;

import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.item.CastCustom;

@Pulse(id = CommonsModule.pulseID, description = "Core feature for all the modules", forced = true)
public class CommonsModule extends PulseBase {
	public static final String pulseID = "ModuleCommons";

	@SidedProxy(clientSide = "knightminer.tcomplement.shared.CommonsClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static ItemMetaDynamic materials;

	public static CastCustom cast, castClay;
	public static ItemStack stoneBucket;
	public static ItemStack castBucket, castBucketClay;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// materials
		materials = registerItem(r, new ItemMetaDynamic(), "materials");
		materials.setCreativeTab(TCompRegistry.tabGeneral);

		// custom casts
		cast = registerItem(r, new CastCustom(), "cast");
		cast.setCreativeTab(TCompRegistry.tabGeneral);

		castClay = registerItem(r, new CastCustom(), "cast_clay");
		castClay.setCreativeTab(TCompRegistry.tabGeneral);

		if(Config.general.bucketCast && isSmelteryLoaded()) {
			stoneBucket = materials.addMeta(0, "stone_bucket");
			castBucket = cast.addMeta(0, "bucket", Material.VALUE_Ingot);
			castBucketClay = castClay.addMeta(0, "bucket", Material.VALUE_Ingot);
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
		if (Config.general.bucketCast && isSmelteryLoaded()) {
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
	}
}
