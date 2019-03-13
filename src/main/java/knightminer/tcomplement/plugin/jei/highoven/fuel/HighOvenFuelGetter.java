package knightminer.tcomplement.plugin.jei.highoven.fuel;

import java.util.List;
import java.util.stream.Collectors;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;

public class HighOvenFuelGetter {
	public static List<HighOvenFuel> getHighOvenFuels() {
		return TCompRegistry.getAllHighOvenFuels().stream()
				.filter((fuel)->!fuel.getFuels().isEmpty())
				.collect(Collectors.toList());
	}
}
