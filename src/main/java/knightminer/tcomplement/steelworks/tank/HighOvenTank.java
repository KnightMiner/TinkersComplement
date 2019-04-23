package knightminer.tcomplement.steelworks.tank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

/**
 * Same as {@link SmelteryTank}, but filters allowed inputs based if its a valid input from the registry
 */
public class HighOvenTank extends SmelteryTank {

	private FluidStack filter;
	private IMixRecipe cache;
	public HighOvenTank(ISmelteryTankHandler parent) {
		super(parent);
	}

	/**
	 * Sets the filter of this tank to the given fluid stack
	 * @param filter  tank filter
	 */
	public void setFilter(@Nullable FluidStack filter) {
		if (filter != null) {
			filter = filter.copy();
		}
		this.filter = filter;
		this.cache = null;
	}

	/**
	 * Checks if the given fluid matches the filter
	 * @param resource  Resource to check
	 * @param update  If true, sets the filter to this fluid if the filter is null
	 * @return  Method of matching the filter. Input means direct match, output means match through mix recipe
	 */
	public FilterMatchType matchesFilter(FluidStack resource, boolean update) {
		// if the filter is null, we need to set the filter
		if(filter == null) {
			if (update) {
				setFilter(resource);
			}
			return FilterMatchType.INPUT;
		} else if(filter.isFluidEqual(resource)) {
			return FilterMatchType.INPUT;
		} else if(cache != null && cache.matches(filter, resource)) {
			return FilterMatchType.OUTPUT;
		}

		// so the fluid is not the input, and does not match our cache, so try another recipe from the registry
		IMixRecipe recipe = TCompRegistry.getMixRecipe(filter, resource);
		if(recipe != null) {
			// found a match? cache it
			this.cache = recipe;
			return FilterMatchType.OUTPUT;

		}

		// leave cache alone, it was probably fine
		return FilterMatchType.NONE;
	}

	/**
	 * Same as {@link #fill(FluidStack, boolean)}, but does not apply the filter
	 */
	public int fillInternal(@Nonnull FluidStack resource, boolean doFill) {
		return super.fill(resource, doFill);
	}

	@Override
	public int fill(@Nonnull FluidStack resource, boolean doFill) {
		if (matchesFilter(resource, doFill) == FilterMatchType.NONE) {
			return 0;
		}
		return fillInternal(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		FluidStack drained = super.drain(resource, doDrain);
		// if draining and that was the last of it, clear the filter
		if(doDrain && drained != null && liquids.isEmpty()) {
			this.setFilter(null);
		}
		return drained;
	}

	private static final String TAG_FILTER = "filter";

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		// write filter
		if(filter != null) {
			NBTTagCompound filterTag = new NBTTagCompound();
			filter.writeToNBT(filterTag);
			tag.setTag(TAG_FILTER, filterTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		// read filter
		if(tag.hasKey(TAG_FILTER, 10)) {
			NBTTagCompound filterTag = tag.getCompoundTag(TAG_FILTER);
			filter = FluidStack.loadFluidStackFromNBT(filterTag);
		}
	}

	public enum FilterMatchType {
		NONE, INPUT, OUTPUT;
	}
}
