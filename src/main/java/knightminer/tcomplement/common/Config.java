package knightminer.tcomplement.common;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import knightminer.tcomplement.TinkersComplement;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import slimeknights.mantle.pulsar.config.ForgeCFG;

@net.minecraftforge.common.config.Config(modid = TinkersComplement.modID, category = "")
public class Config {
	@Ignore
	public static ForgeCFG pulseConfig = new ForgeCFG("TComplementModules", "Modules");

	@Comment("General configuration options")
	@LangKey("tcomplement.config.general")
	public static General general = new General();

	@Comment("Options to configure the melter from the melter module")
	@LangKey("tcomplement.config.melter")
	public static Melter melter = new Melter();

	@Comment("Options to configure the high oven from the steelworks module")
	@LangKey("tcomplement.config.highOven")
	public static HighOven highOven = new HighOven();

	@Comment("Options to configure JEI integration")
	@LangKey("tcomplement.config.jei")
	public static JEI jei = new JEI();

	public static class General {
		@RequiresMcRestart
		@Comment("Enables the bucket cast: allows casting buckets using a casting table.")
		@LangKey("tcomplement.config.general.bucketCast")
		public boolean bucketCast = true;
	}

	public static class Melter {
		@RequiresMcRestart
		@Comment("Ratio of ore to material produced in the melter.")
		@RangeDouble(min = 0, max = 16)
		@LangKey("tcomplement.config.oreToIngotRatio")
		public double oreToIngotRatio = 1.0f;

		@RequiresMcRestart
		@Comment("Disallows creating seared stone in the melter using cobblestone or tool parts.")
		@LangKey("tcomplement.config.melter.blacklistStone")
		public boolean blacklistStone = true;
	}

	public static class HighOven {
		@RequiresMcRestart
		@Comment("Ratio of ore to material produced in the high oven.")
		@RangeDouble(min = 0, max = 16)
		@LangKey("tcomplement.config.oreToIngotRatio")
		public double oreToIngotRatio = 2.5f;
	}

	public static class JEI {
		@RequiresMcRestart
		@Comment({
			"If true, puts the melter in its own recipe tab in JEI to make the blacklist and overrides more clear.",
			"If false, the melter is just added to the normal Tinkers tab."
		})
		@LangKey("tcomplement.config.jei.separateMelterTab")
		public boolean separateMelterTab = true;
	}

	public static class ConfigProperty implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String option = JsonUtils.getString(json, "option");
			return () -> {
				switch(option) {
					case "bucket_cast": return general.bucketCast;
				}
				throw new JsonSyntaxException("Config option '" + option + "' does not exist");
			};
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
