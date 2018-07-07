package knightminer.tcomplement.library.tanks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;

public class AlloyTank extends FluidHandlerConcatenate {

	private final FluidTankAnimated[] tanks;
	public AlloyTank(FluidTankAnimated... tanks) {
		super(tanks);
		this.tanks = tanks;
	}

	public AlloyTank(Collection<FluidTankAnimated> newTanks) {
		this(newTanks.toArray(new FluidTankAnimated[newTanks.size()]));
	}

	public List<FluidStack> getFluids() {
		FluidStack fluid;
		List<FluidStack> fluids = new ArrayList<>(tanks.length);
		for(FluidTankAnimated tank : tanks) {
			fluid = tank.getFluid();
			if(fluid != null) {
				fluids.add(tank.getFluid());
			}
		}
		return fluids;
	}
}
