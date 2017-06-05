package knightminer.knightsconstruct.shared;

import com.google.common.eventbus.Subscribe;

import knightminer.knightsconstruct.common.CommonProxy;
import knightminer.knightsconstruct.common.KnightsPulse;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.item.CastCustom;

@Pulse(id = KnightsCommons.pulseID, description = "Core feature for all the modules", forced = true)
public class KnightsCommons extends KnightsPulse {
	public static final String pulseID = "KnightsCommons";

	@SidedProxy(clientSide = "knightminer.knightsconstruct.shared.CommonsClientProxy", serverSide = "knightminer.knightsconstruct.common.CommonProxy")
	public static CommonProxy proxy;

	//public static ItemMetaDynamic materials;

	public static CastCustom castCustom;
	public static ItemStack castBucket;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {

		// materials
		//materials = registerItem(new ItemMetaDynamic(), "materials");

		// custom casts
		castCustom = registerItem(new CastCustom(), "cast");

		if(isFeaturesLoaded()) {
			castBucket = castCustom.addMeta(0, "bucket", Material.VALUE_Ingot);
		}

		proxy.preInit();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
}
