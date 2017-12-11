package knightminer.tcomplement.plugin.chisel;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.plugin.chisel.items.ItemChisel;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = ChiselPlugin.pulseID, description = "Add a Tinkers version of the Chisel Chisel", modsRequired = "chisel")
public class ChiselPlugin extends PulseBase {
	public static final String pulseID = "ChiselPlugin";

	@SidedProxy(clientSide = "knightminer.tcomplement.plugin.chisel.ChiselPluginClientProxy", serverSide = "knightminer.tcomplement.common.CommonProxy")
	public static CommonProxy proxy;

	public static ToolPart chiselHead;
	public static ToolCore chisel;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(isToolsLoaded()) {
			chiselHead = registerItem(r, new ToolPart(Material.VALUE_Ingot), "chisel_head");
			chiselHead.setCreativeTab(TCompRegistry.tabTools);
			chisel = registerItem(r, new ItemChisel(), "chisel");
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		if(isToolsLoaded()) {
			TinkerRegistry.registerToolCrafting(chisel);
			registerStencil(chiselHead);

			TCompRegistry.tabTools.setDisplayIcon(chisel.buildItem(ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.iron)));
		}
		proxy.init();
	}
}
