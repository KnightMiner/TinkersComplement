package knightminer.tcomplement.library;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHeaterConsumer {
	/**
	 * Gets the current percentage of fuel in the consumer
	 * @return  Float value between 0 and 1
	 */
	@SideOnly(Side.CLIENT)
	float getFuelPercentage();

	/**
	 * Checks if the consumer has fuel
	 * @return  True if the consumer has fuel
	 */
	boolean hasFuel();

	/**
	 * Gets the current fuel amount for syncing
	 * @return  fuel amount
	 */
	int getFuel();

	/**
	 * Gets the current fuel quality for syncing
	 * @return  amount current fuel produces when consumed
	 */
	int getFuelQuality();

	/**
	 * Updates the fuel from the server side
	 * @param index  0 for fuel, 1 for fuel quality
	 * @param fuel   New value
	 */
	@SideOnly(Side.CLIENT)
	void updateFuelFromPacket(int index, int fuel);
}
