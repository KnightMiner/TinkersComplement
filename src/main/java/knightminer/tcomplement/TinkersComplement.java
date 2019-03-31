package knightminer.tcomplement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.tcomplement.armor.ArmorModule;
import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.TCompNetwork;
import knightminer.tcomplement.melter.MelterModule;
import knightminer.tcomplement.plugin.ceramics.CeramicsPlugin;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import knightminer.tcomplement.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcomplement.shared.CommonsModule;
import knightminer.tcomplement.shared.OredictModule;
import knightminer.tcomplement.shared.legacy.TileEntityRenamer;
import knightminer.tcomplement.steelworks.SteelworksModule;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = TinkersComplement.modID,
		name = TinkersComplement.modName,
		version = TinkersComplement.modVersion,
		dependencies = "required-after:forge@[14.23.4.2705,);"
				+ "required-after:mantle@[1.12-1.3.3.49,);"
				+ "required-after:tconstruct@[1.12.2-2.12.0.135,);"
				+ "after:chisel@[MC1.12-0.1.0.22,);"
				+ "after:exnihilocreatio",
				acceptedMinecraftVersions = "[1.12.2, 1.13)")
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
		pulseManager.registerPulse(new CommonsModule());
		pulseManager.registerPulse(new MelterModule());
		pulseManager.registerPulse(new ArmorModule());
		pulseManager.registerPulse(new SteelworksModule());
		pulseManager.registerPulse(new CeramicsPlugin());
		pulseManager.registerPulse(new ChiselPlugin());
		pulseManager.registerPulse(new ExNihiloPlugin());
		pulseManager.registerPulse(new OredictModule());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// config syncing
		MinecraftForge.EVENT_BUS.register(this);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
		TCompNetwork.instance.setup();

		ModFixs fixer = FMLCommonHandler.instance().getDataFixer().init(modID, 1);
		fixer.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityRenamer());
	}

	@SubscribeEvent
	public void onConfigChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(modID)) {
			ConfigManager.sync(modID, Type.INSTANCE);
		}
	}
}
