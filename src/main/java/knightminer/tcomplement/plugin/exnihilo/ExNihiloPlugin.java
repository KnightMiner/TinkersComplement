package knightminer.tcomplement.plugin.exnihilo;

import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.plugin.exnihilo.items.SledgeHammer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

@Pulse(id = ExNihiloPlugin.pulseID, description = "Adds an Tinkers Construct version of the Ex Nihilo hammer", modsRequired = "exnihilocreatio")
public class ExNihiloPlugin extends PulseBase {
	public static final String pulseID = "ExNihiloPlugin";

	@SidedProxy(clientSide = "knightminer.tcomplement.plugin.exnihilo.ENPluginClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static ToolPart sledgeHead;
	public static ToolCore sledgeHammer;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		sledgeHead = registerItem(r, new ToolPart(Material.VALUE_Ingot * 2), "sledge_head");
		sledgeHead.setCreativeTab(TCompRegistry.tabGeneral);
		sledgeHammer = registerItem(r, new SledgeHammer(), "sledge_hammer");
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		TinkerRegistry.registerToolCrafting(sledgeHammer);
		registerStencil(sledgeHead);

		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ENPluginEvents());
	}
}
