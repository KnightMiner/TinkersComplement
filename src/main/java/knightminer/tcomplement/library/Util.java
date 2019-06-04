package knightminer.tcomplement.library;

import knightminer.tcomplement.TinkersComplement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

import static slimeknights.tconstruct.library.Util.temperatureString;

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

	/**
	 * Calculates the temperature text color based on temperature, based on code from Tinkers' Steelworks
	 * @param temp Temperature in Celsius
	 * @return Color integer for temperature
	 */
	public static int getHighOvenTempColor(int temp) {
		if (temp > 2000) return 0xFF0000;

		// shift the temperature to have a gradient from 0 -> 1980 (which will visually give 20 -> 2000)
		float percent = (temp - 20) / 1980F;

		// 0xFF0000 <- 0x404040
		int r = (int) ((0xFF - 0x40) * percent) + 0x40;
		int gb = (int) ((0x00 - 0x40) * percent) + 0x40;

		return r << 16 | gb << 8 | gb;
	}

	/**
	 * Alias for the TConstruct util method, switching units to celsius
	 * @param temperature  Temperature in Celsius
	 * @return  Formatted string
	 */
	public static String celsiusString(int temperature) {
		return temperatureString(temperature+300);
	}

	/**
	 * Shared logic to interact with a fluid tank on right click
	 * @param world    World instance
	 * @param pos      Location of the block
	 * @param player   Player interacting
	 * @param hand     Hand used to interact
	 * @param facing   Side of the block clicked
	 * @return  True if it interacted, false if we are not holding a fluid container
	 */
	public static boolean onFluidTankActivated(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing) {
		TileEntity te = world.getTileEntity(pos);
		if(te == null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) {
			return false;
		}

		IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
		return FluidUtil.interactWithFluidHandler(player, hand, fluidHandler);
	}

	/**
	 * Performs integer division on two numbers, rounding any remainder up
	 *
	 * @param dividend Number being divided
	 * @param divisor  Number dividing
	 * @return Result rounded up
	 */
	public static int ceilDiv(int dividend, int divisor) {
		return dividend / divisor + (dividend % divisor == 0 ? 0 : 1);
	}
}
