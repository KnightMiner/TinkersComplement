package knightminer.tcompliment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.tcompliment.common.Config;
import knightminer.tcompliment.common.TCompNetwork;
import knightminer.tcompliment.feature.ModuleFeature;
import knightminer.tcompliment.plugin.ceramics.CeramicsPlugin;
import knightminer.tcompliment.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcompliment.shared.ModuleCommons;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = TinkersCompliment.modID,
		name = TinkersCompliment.modName,
		version = TinkersCompliment.modVersion,
		dependencies = "required-after:forge;"
				+ "required-after:mantle;"
				+ "required-after:tconstruct;"
				+ "after:exnihiloadscensio",
				acceptedMinecraftVersions = "[1.11.2, 1.12)")
public class TinkersCompliment {
	public static final String modID = "tcompliment";
	public static final String modVersion = "${version}";
	public static final String modName = "Tinkers' Compliment";

	public static final Logger log = LogManager.getLogger(modID);

	@Mod.Instance(modID)
	public static TinkersCompliment instance;

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
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

		TCompNetwork.instance.setup();
	}
}
