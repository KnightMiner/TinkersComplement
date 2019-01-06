package knightminer.tcomplement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import knightminer.tcomplement.common.Config;
import knightminer.tcomplement.common.TCompNetwork;
import knightminer.tcomplement.feature.ModuleFeature;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import knightminer.tcomplement.plugin.ceramics.CeramicsPlugin;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import knightminer.tcomplement.plugin.exnihilo.ExNihiloPlugin;
import knightminer.tcomplement.shared.ModuleCommons;
import knightminer.tcomplement.shared.legacy.TileEntityRenamer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.pulsar.control.PulseManager;

@Mod(
		modid = TinkersComplement.modID,
		name = TinkersComplement.modName,
		version = TinkersComplement.modVersion,
		dependencies = "required-after:forge@[14.23.4.2705,);"
				+ "required-after:mantle;"
				+ "required-after:tconstruct@[1.12-2.7.4.38,);"
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
		pulseManager.registerPulse(new ModuleCommons());
		pulseManager.registerPulse(new ModuleFeature());
		pulseManager.registerPulse(new CeramicsPlugin());
		pulseManager.registerPulse(new ChiselPlugin());
		pulseManager.registerPulse(new ExNihiloPlugin());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.load(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

		TCompNetwork.instance.setup();

		ModFixs fixer = FMLCommonHandler.instance().getDataFixer().init(modID, 1);
		fixer.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityRenamer());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		TileMelter.init();
  }
}
