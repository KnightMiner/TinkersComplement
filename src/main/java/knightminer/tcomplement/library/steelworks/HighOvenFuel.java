package knightminer.tcomplement.library.steelworks;

import java.util.List;

import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.utils.ListUtil;

public class HighOvenFuel {
	private int time, rate;
	private RecipeMatch fuel;

	/**
	 * Creates a new high oven fuel
	 * @param fuel  Fuel recipe match entry
	 * @param time  Fuel burn time in seconds
	 * @param rate  Temperature increase per second
	 */
	public HighOvenFuel(RecipeMatch fuel, int time, int rate) {
		this.fuel = fuel;
		this.time = time;
		this.rate = rate;
	}

	/**
	 * Checks if the given fuel matches this entry
	 * @param fuel  ItemStack fuel
	 * @return  true if the stack matches
	 */
	public boolean matches(ItemStack fuel) {
		return this.fuel.matches(ListUtil.getListFrom(fuel)).isPresent();
	}

	/**
	 * Gets the burn time of this fuel. This is depleted once every for game ticks
	 * @return  burn time in in seconds
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Gets the amount of degrees per tick of this fuel
	 * @return  degrees per seconds
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * Gets a list of all inputs valid for this fuel, for display in JEI
	 * @return  List of stacks matching this fuel
	 */
	public List<ItemStack> getFuels() {
		return fuel.getInputs();
	}
}
