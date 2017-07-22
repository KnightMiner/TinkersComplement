package knightminer.tcomplement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.TCompNetwork;
import knightminer.tcomplement.feature.ModuleFeature;
import knightminer.tcomplement.plugin.ceramics.CeramicsPlugin;
import knightminer.tcomplement.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcomplement.shared.ModuleCommons;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = TinkersComplement.modID,
		name = TinkersComplement.modName,
		version = TinkersComplement.modVersion,
		dependencies = "required-after:forge;"
				+ "required-after:mantle;"
				+ "required-after:tconstruct;"
				+ "after:exnihiloadscensio",
				acceptedMinecraftVersions = "[1.11.2, 1.12)")
public class TinkersComplement {
	public static final String modID = "tcomplement";
	public static final String modVersion = "${version}";
	public static final String modName = "Tinkers' Complement";

	public static final Logger log = LogManager.getLogger(modID);

	@Mod.Instance(modID)
	public static TinkersComplement instance;

	public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
	public static GuiHandler guiHandler = new GuiHandler();

	static {
		pulseManager.registerPulse(new ModuleCommons());
		pulseManager.registerPulse(new ModuleFeature());
		pulseManager.registerPulse(new ExNihiloPlugin());
		pulseManager.registerPulse(new CeramicsPlugin());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.load(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

		TCompNetwork.instance.setup();
	}

	// Old version compatibility
	@Mod.EventHandler
	public void onMissingMapping(FMLMissingMappingsEvent event) {
		// TODO: safe to remove when I update to 1.12
		// thanks to /u/Thiakil on reddit for this code
		for (FMLMissingMappingsEvent.MissingMapping mapping : event.getAll()){
			if (mapping.resourceLocation.getResourceDomain().equals( "tcompliment" )){
				ResourceLocation newLoc = new ResourceLocation( modID, mapping.resourceLocation.getResourcePath() );
				switch( mapping.type ){
					case ITEM:
						Item newItem = Item.REGISTRY.getObject( newLoc );
						if (newItem != null)
							mapping.remap( newItem );
						else
							mapping.warn();
						break;
					case BLOCK:
						Block newBlock = Block.REGISTRY.getObject( newLoc );
						if (newBlock != null && newBlock != Blocks.AIR)
							mapping.remap( newBlock );
						else
							mapping.warn();
				}
			}
		}
	}
}
