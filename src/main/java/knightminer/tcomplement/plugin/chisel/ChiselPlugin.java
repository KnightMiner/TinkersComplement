package knightminer.tcomplement.plugin.chisel;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import knightminer.tcomplement.common.CommonProxy;
import knightminer.tcomplement.common.PulseBase;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.plugin.chisel.items.ItemChisel;
import knightminer.tcomplement.steelworks.SteelworksModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.smeltery.block.BlockSeared.SearedType;
import slimeknights.tconstruct.tools.TinkerMaterials;

@Pulse(id = ChiselPlugin.pulseID, description = "Add a Tinkers version of the Chisel Chisel", modsRequired = "chisel")
public class ChiselPlugin extends PulseBase {
	public static final String modid = "chisel";
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

		// allow chiseling scorched blocks
		if(isSteelworksLoaded()) {
			for(SearedType type : SearedType.values()) {
				// skip cobble since its a bit out of place
				if(type != SearedType.COBBLE) {
					addChiselVariation(SteelworksModule.scorchedBlock, type.getMeta(), "scorched_block");
				}
			}
		}

		proxy.init();
	}

	protected void addChiselVariation(Block block, int meta, String groupName) {
		if(block != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("group", groupName);
			nbt.setTag("stack", new ItemStack(block, 1, meta).writeToNBT(new NBTTagCompound()));
			nbt.setString("block", block.getRegistryName().toString());
			nbt.setInteger("meta", meta);
			FMLInterModComms.sendMessage(modid, "add_variation", nbt);
		}
	}
}
