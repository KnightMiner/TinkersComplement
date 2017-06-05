package knightminer.knightsconstruct.plugin.ceramics;

import com.google.common.eventbus.Subscribe;

import knightminer.knightsconstruct.common.CommonProxy;
import knightminer.knightsconstruct.common.KnightsPulse;
import knightminer.knightsconstruct.common.ModIds;
import knightminer.knightsconstruct.library.KnightsRegistry;
import knightminer.knightsconstruct.shared.KnightsCommons;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockCasting.CastingType;

@Pulse(id = CeramicsPlugin.pulseID, description = "Adds casting supplies made from porcelain", modsRequired = ModIds.Ceramics.ID)
public class CeramicsPlugin extends KnightsPulse {
	public static final String pulseID = "CeramicsPlugin";

	@SidedProxy(clientSide = "knightminer.knightsconstruct.plugin.ceramics.CeramicsPluginClientProxy", serverSide = "knightminer.knightsconstruct.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block porcelainCasting;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		porcelainCasting = registerBlock(new BlockCasting(), "porcelain_casting", BlockCasting.TYPE);
		porcelainCasting.setCreativeTab(KnightsRegistry.tabGeneral);

		proxy.preInit();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		ItemStack porcelainBrick = GameRegistry.makeItemStack(ModIds.Ceramics.clayUnfired, ModIds.Ceramics.porcelainMeta, 1, null);
		if(!porcelainBrick.isEmpty()) {
			GameRegistry.addRecipe(new ItemStack(porcelainCasting, 1, CastingType.TABLE.getMeta()),
					"bbb", "b b", "b b", 'b', porcelainBrick); // Table
			GameRegistry.addRecipe(new ItemStack(porcelainCasting, 1, CastingType.BASIN.getMeta()),
					"b b", "b b", "bbb", 'b', porcelainBrick.copy()); // Basin

		}
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		if(KnightsCommons.castBucket != null && isSmelteryLoaded()) {
			ItemStack bucket = GameRegistry.makeItemStack(ModIds.Ceramics.bucket, 0, 1, null);
			if(!bucket.isEmpty()) {
				// register remaining cast creation
				for(FluidStack fs : TinkerSmeltery.castCreationFluids) {
					TinkerRegistry.registerTableCasting(new CastingRecipe(KnightsCommons.castBucket, new RecipeMatch.Item(bucket, 1), fs, true, true));
				}

				TinkerRegistry.registerTableCasting(bucket, KnightsCommons.castBucket, TinkerFluids.clay, Material.VALUE_Ingot * 3);
			}
		}
	}
}
