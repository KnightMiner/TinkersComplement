package knightminer.tcomplement.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import knightminer.tcomplement.TinkersComplement;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;

public class Config {

	public static ForgeCFG pulseConfig = new ForgeCFG("TComplementModules", "Modules");

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


	public static class PulseLoaded implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String pulse = JsonUtils.getString(json, "pulse");
			return () -> TinkersComplement.pulseManager.isPulseLoaded(pulse);
		}
	}
}
