package knightminer.tcompliment.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {

	public static ForgeCFG pulseConfig = new ForgeCFG("TinkersComplimentModules", "Modules");

	public static float oreToIngotRatio = 1.0f;

	public static boolean blacklistMelterStone = false;

	static Configuration configFile;

	public static void load(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		blacklistMelterStone = configFile.getBoolean("blacklistStone", "melter", true,
				"Disallows creating seared stone in the melter using cobblestone or tool parts");

		oreToIngotRatio = configFile.getFloat("oreToIngotRatio", "melter", 1.0f, 0f, 16.0f,
				"Ratio of ore to material produced in the melter.");

		if(configFile.hasChanged()) {
			configFile.save();
		}
	}
}
