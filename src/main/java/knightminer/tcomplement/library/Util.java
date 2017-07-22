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
}
