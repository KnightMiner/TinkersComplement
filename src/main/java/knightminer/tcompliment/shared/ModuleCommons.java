package knightminer.tcompliment.shared;

import com.google.common.eventbus.Subscribe;

import knightminer.tcompliment.common.CommonProxy;
import knightminer.tcompliment.common.PulseBase;
import knightminer.tcompliment.library.TCompRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.item.CastCustom;

@Pulse(id = ModuleCommons.pulseID, description = "Core feature for all the modules", forced = true)
public class ModuleCommons extends PulseBase {
	public static final String pulseID = "KnightsCommons";

	@SidedProxy(clientSide = "knightminer.tcompliment.shared.CommonsClientProxy", serverSide = "knightminer.tcompliment.common.CommonProxy")
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
		castCustom.setCreativeTab(TCompRegistry.tabGeneral);

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
