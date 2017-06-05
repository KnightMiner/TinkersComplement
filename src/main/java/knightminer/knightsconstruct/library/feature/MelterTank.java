package knightminer.knightsconstruct.library.feature;

import knightminer.knightsconstruct.common.KnightsNetwork;
import knightminer.knightsconstruct.feature.network.FluidUpdatePacket;
import knightminer.knightsconstruct.feature.tileentity.TileMelter;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;

public class MelterTank extends FluidTankAnimated {

	private TileMelter parent;
	public MelterTank(int capacity, TileMelter parent) {
		super(capacity, parent);
		this.parent = parent;
	}

	@Override
	protected void sendUpdate(int amount) {
		if(amount != 0) {
			renderOffset += amount;
			// packet is sent on all changes as the server side often adds fluids
		}
	}

	@Override
	protected void onContentsChanged() {
		super.onContentsChanged();
		if(parent.isServerWorld()) {
			KnightsNetwork.sendToAll(new FluidUpdatePacket(parent.getPos(), this.getFluid()));
		}
	}
}
