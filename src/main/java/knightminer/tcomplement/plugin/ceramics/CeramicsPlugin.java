package knightminer.tcomplement.plugin.ceramics;

import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.ModIds;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.shared.ModuleCommons;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockCasting.CastingType;

@Pulse(id = CeramicsPlugin.pulseID, description = "Adds casting supplies made from porcelain", modsRequired = ModIds.Ceramics.ID)
public class CeramicsPlugin extends PulseBase {
	public static final String pulseID = "CeramicsPlugin";

	@SidedProxy(clientSide = "knightminer.tcomplement.plugin.ceramics.CeramicsPluginClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static Block porcelainCasting;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		porcelainCasting = registerBlock(new BlockCasting(), "porcelain_casting", BlockCasting.TYPE);
		porcelainCasting.setCreativeTab(TCompRegistry.tabGeneral);

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
		if(ModuleCommons.castBucket != null && isSmelteryLoaded()) {
			ItemStack bucket = GameRegistry.makeItemStack(ModIds.Ceramics.bucket, 0, 1, null);
			if(!bucket.isEmpty()) {
				TinkerRegistry.registerTableCasting(bucket, ModuleCommons.castBucket, TinkerFluids.clay, Material.VALUE_Ingot * 3);
			}
		}
	}
}
