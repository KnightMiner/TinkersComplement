package knightminer.knightsconstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.knightsconstruct.common.Config;
import knightminer.knightsconstruct.common.KnightsNetwork;
import knightminer.knightsconstruct.feature.KnightsFeature;
import knightminer.knightsconstruct.plugin.ceramics.CeramicsPlugin;
import knightminer.knightsconstruct.plugin.exnihilo.ExNihiloPlugin;
import knightminer.knightsconstruct.shared.KnightsCommons;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = KnightsConstruct.modID,
		name = KnightsConstruct.modName,
		version = KnightsConstruct.modVersion,
		dependencies = "required-after:forge;"
				+ "required-after:mantle;"
				+ "required-after:tconstruct;"
				+ "after:exnihiloadscensio",
				acceptedMinecraftVersions = "[1.11.2, 1.12)")
public class KnightsConstruct {
	public static final String modID = "kconstruct";
	public static final String modVersion = "${version}";
	public static final String modName = "Knights' Construct";

	public static final Logger log = LogManager.getLogger(modID);

	@Mod.Instance(modID)
	public static KnightsConstruct instance;

	public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);
	public static GuiHandler guiHandler = new GuiHandler();

	static {
		pulseManager.registerPulse(new KnightsCommons());
		pulseManager.registerPulse(new KnightsFeature());
		pulseManager.registerPulse(new ExNihiloPlugin());
		pulseManager.registerPulse(new CeramicsPlugin());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

		KnightsNetwork.instance.setup();
	}
}
