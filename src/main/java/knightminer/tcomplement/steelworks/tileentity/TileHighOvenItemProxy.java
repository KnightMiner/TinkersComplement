package knightminer.tcomplement.steelworks.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

/**
 * Allows access to inventories in the high oven
 */
public abstract class TileHighOvenItemProxy extends TileSmelteryComponent {

	/** Gets the inventory associated with this inventory reader */
	protected abstract IItemHandlerModifiable getInventory(TileHighOven tile);

	/**
	 * Gets a high oven TE at the position of the master
	 * @return null if the TE is not TileHighOven or if the master is missing
	 */
	protected TileHighOven getHighOven() {
		if(getHasMaster()) {
			TileEntity te = getWorld().getTileEntity(getMasterPosition());
			if(te instanceof TileHighOven) {
				return (TileHighOven)te;
			}
		}
		return null;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return getHighOven() != null;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			TileHighOven te = this.getHighOven();
			if(te == null) {
				return super.getCapability(capability, facing);
			}

			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getInventory(te));
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Allows access to the high oven's side main inventory to add items
	 */
	public static class TileChute extends TileHighOvenItemProxy {
		@Override
		protected IItemHandlerModifiable getInventory(TileHighOven tile) {
			return tile.getItemHandler();
		}
	}

	/**
	 * Allows access to the high oven's additive inventory for oxidizers, reducers, purifiers, and fuels
	 */
	public static class TileDuct extends TileHighOvenItemProxy {
		@Override
		protected IItemHandlerModifiable getInventory(TileHighOven tile) {
			return tile.getAdditives();
		}
	}
}
