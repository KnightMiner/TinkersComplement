package knightminer.tcomplement.plugin.ceramics;

import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.ModIds;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.shared.ModuleCommons;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.BlockCasting;

@Pulse(id = CeramicsPlugin.pulseID, description = "Adds casting supplies made from porcelain", modsRequired = ModIds.Ceramics.ID)
public class CeramicsPlugin extends PulseBase {
	public static final String pulseID = "CeramicsPlugin";

	@SidedProxy(clientSide = "knightminer.tcomplement.plugin.ceramics.CeramicsPluginClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block porcelainCasting;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		porcelainCasting = registerBlock(r, new BlockCasting(), "porcelain_casting");
		porcelainCasting.setCreativeTab(TCompRegistry.tabGeneral);
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		registerItemBlock(r, porcelainCasting, BlockCasting.TYPE);
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		if(Config.general.bucketCast && isSmelteryLoaded()) {
			ItemStack bucket = GameRegistry.makeItemStack(ModIds.Ceramics.bucket, 0, 1, null);
			if(!bucket.isEmpty()) {
				TinkerRegistry.registerTableCasting(bucket, ModuleCommons.castBucket, TinkerFluids.clay, Material.VALUE_Ingot * 3);
				TinkerRegistry.registerTableCasting(new CastingRecipe(bucket.copy(), RecipeMatch.of(ModuleCommons.castBucketClay), TinkerFluids.clay, Material.VALUE_Ingot * 3, true, false));
			}
		}
	}
}
