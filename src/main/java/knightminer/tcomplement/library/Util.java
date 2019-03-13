package knightminer.tcomplement.library;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import knightminer.tcomplement.TinkersComplement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class Util {
	public static String resource(String name) {
		return String.format("%s:%s", TinkersComplement.modID, name.toLowerCase(Locale.US));
	}
	public static String prefix(String name) {
		return String.format("%s.%s", TinkersComplement.modID, name.toLowerCase(Locale.US));
	}

	public static ResourceLocation getResource(String res) {
		return new ResourceLocation(TinkersComplement.modID, res);
	}

	/**
	 * Translate the string, insert parameters into the translation key
	 */
	public static String translate(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		return I18n.translateToLocal(I18n.translateToLocal(String.format(key, pars)).trim()).trim();
	}

	/**
	 * Translate the string, insert parameters into the result of the translation
	 */
	public static String translateFormatted(String key, Object... pars) {
		// translates twice to allow rerouting/alias
		return I18n.translateToLocal(I18n.translateToLocalFormatted(key, pars).trim()).trim();
	}

	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

	}

	public static Logger getLogger(String type) {
		String log = TinkersComplement.modID;

		return LogManager.getLogger(log + "-" + type);
	}

	/** Calculates the temperature text color based on temperature, based on code from Tinkers' Steelworks */
	public static int getHighOvenTempColor(int temp) {
		if (temp > 2000) return 0xFF0000;

		// shift the temperature to have a gradient from 0 -> 1980 (which will visually give 20 -> 2000)
		float percent = (temp - 20) / 1980F;

		// 0xFF0000 <- 0x404040
		int r = (int) ((0xFF - 0x40) * percent) + 0x40;
		int gb = (int) ((0x00 - 0x40) * percent) + 0x40;

		return r << 16 | gb << 8 | gb;
	}
}
