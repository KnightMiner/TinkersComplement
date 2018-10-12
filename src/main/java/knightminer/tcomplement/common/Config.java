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
	public static boolean blacklistMelterStone = true;
	public static String[] heaterFuels;
	public static int defaultHeaterFuelTemperature;

	static Configuration configFile;

	public static void load(FMLPreInitializationEvent event) {
		configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

		// melter
		blacklistMelterStone = configFile.getBoolean("blacklistStone", "melter", true,
				"Disallows creating seared stone in the melter using cobblestone or tool parts");
		oreToIngotRatio = configFile.getFloat("oreToIngotRatio", "melter", 1.0f, 0f, 16.0f,
				"Ratio of ore to material produced in the melter.");
		heaterFuels = configFile.getStringList("heaterFuels", "melter", new String[]{}, "List of fuels that can be used in the heater and their respective temp (in Celcius).items mod:xxx[:data_value]=temp\nFuels in this list require to be valid minecraft furnace fuels.");
		defaultHeaterFuelTemperature = configFile.getInt("defaultHeaterFuelTemperature", "melter", 200,0, Integer.MAX_VALUE, "Default temperature (in Celcius) for any valid fuel that is not registred in heaterFuels.\nIf set to 0, disable all heater fuels but the ones in heaterFuels.\n200 is meant to be just about enough to melt clay or most metals, but not iron");

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
