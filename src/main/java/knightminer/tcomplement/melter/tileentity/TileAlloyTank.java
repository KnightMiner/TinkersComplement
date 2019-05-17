package knightminer.tcomplement.melter.tileentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.library.tanks.AlloyTank;
import knightminer.tcomplement.library.tanks.FluidHandlerDrainOnlyWrapper;
import knightminer.tcomplement.melter.blocks.BlockAlloyTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TileAlloyTank extends TileTank implements ITickable, IHeaterConsumer {

	protected static final int ALLOYING_PER_TICK = 10;

	protected int tick = 0;
	private boolean active, needsFuel;
	private int fuel, fuelQuality;

	private List<BlockPos> tanks;
	private AlloyTank alloyTank;
	private IFluidHandler tankWrapper;
	private AlloyRecipe currentRecipe;
	public TileAlloyTank() {
		super();
		tanks = Lists.newLinkedList();
		this.tankWrapper = new FluidHandlerDrainOnlyWrapper(tank);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void update() {
		if(world == null || world.isRemote) {
			return;
		}

		// don't alloy if not active (missing heater or powered)
		if (!active) {
			return;
		}

		// alloying time
		if(tick % 4 == 0) {
			// if we currently don't have fuel, try getting some
			if (needsFuel) {
				consumeFuel();
			}

			// alloy time!

			// tank must not be full
			if(tank.getFluidAmount() != CAPACITY) {
				// must have side tanks, though if we got this far we should
				AlloyTank alloyTank = getAlloyTank();
				if(alloyTank != null) {
					// must have fluids in those side tanks, we also know no alloy recipe takes just one fluid
					List<FluidStack> inputs = alloyTank.getFluids();
					if(inputs.size() >= 2) {
						// finally, time to try recipes
						FluidStack current = tank.getFluid();

						// if we have a cached recipe, use that
						if(currentRecipe == null || !tryRecipe(currentRecipe, current, inputs, alloyTank)) {
							// recipe failed, so its no longer our current
							currentRecipe = null;

							// try and find a new one
							for(AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
								if(tryRecipe(recipe, current, inputs, alloyTank)) {
									// store that recipe to save time later
									currentRecipe = recipe;
									break;
								}
							}
						}
					}
				}
			}
		}

		tick = (tick + 1) % 20;
	}

	/**
	 * Attempts a recipe with the current inputs
	 * @param recipe    Recipe to try
	 * @param current   Current output contents
	 * @param inputs    List of inputs
	 * @param alloyTank Alloy tank instance for inputs
	 * @return
	 */
	private boolean tryRecipe(AlloyRecipe recipe, @Nullable FluidStack current, @Nonnull List<FluidStack> inputs, @Nonnull AlloyTank alloyTank) {
		// bad recipe
		if(!recipe.isValid()) {
			return false;
		}

		// fluid does not match or is somehow invalid
		FluidStack result = FluidUtil.getValidFluidStackOrNull(recipe.getResult());
		if(result == null || (current != null && !result.isFluidEqual(current))) {
			return false;
		}

		// check if the recipe matches
		int matched = recipe.matches(inputs);
		if(matched <= 0) {
			return false;
		}

		// matches, but no space
		int remaining = current == null ? CAPACITY : CAPACITY - current.amount;
		if (remaining < result.amount) {
			return true;
		}

		// matches, but no fuel
		if(!hasFuel()) {
			needsFuel = true;
			return true;
		}

		// finally apply the recipe
		// recipe is limited in times per tick and cannot beyond a full tank
		matched = Math.min(matched * result.amount, Math.min(ALLOYING_PER_TICK, remaining));
		do {
			// remove all liquids from the tank
			for(FluidStack liquid : recipe.getFluids()) {
				FluidStack toDrain = liquid.copy();
				FluidStack drained = alloyTank.drain(toDrain, true);
				// error logging
				assert drained != null;
				if(!drained.isFluidEqual(toDrain) || drained.amount != toDrain.amount) {
					TinkersComplement.log.error("Melter alloy creation drained incorrect amount: was {}:{}, should be {}:{}", drained
							.getUnlocalizedName(), drained.amount, toDrain.getUnlocalizedName(), toDrain.amount);
				}
			}

			// and insert the alloy
			FluidStack toFill = result.copy();
			int filled = tank.fill(toFill, true);
			if(filled != result.amount) {
				TinkersComplement.log.error("Melter alloy creation filled incorrect amount: was {}, should be {} ({})", filled,
						recipe.getResult().amount * matched, result.getUnlocalizedName());
			}
			matched -= filled;
		} while(matched > 0);
		fuel--;
		return true;
	}

	private void consumeFuel() {
		// if we already have fuel, we don't need it
		if(hasFuel()) {
			this.needsFuel = false;
			return;
		}

		World world = getWorld();
		if(world == null) {
			return;
		}

		// find our heater and extract fuel
		TileEntity te = world.getTileEntity(this.pos.down());
		if (te instanceof TileTank) {
			IFluidTank tank = ((TileTank) te).getInternalTank();
			FluidStack liquid = tank.getFluid();
			if(liquid != null) {
				// first, attempt to consume some fuel
				FluidStack in = liquid.copy();
				int fuel = TinkerRegistry.consumeSmelteryFuel(in);
				if(fuel > 0) {
					// if successful, try draining that from the tank
					int amount = liquid.amount - in.amount;
					FluidStack drained = tank.drain(amount, false);
					if(drained != null && drained.amount == amount) {
						// boost fuel to match the amount we consume by below
						// see the comment in TileMelter for more information
						fuel *= 2.5;
						this.fuel += fuel;
						this.fuelQuality = fuel;

						// actually remove the fuel
						tank.drain(amount, true);

						return;
					}
				}
			}
		} else if(te instanceof TileHeater) {
			int time = ((TileHeater)te).consumeFuel();
			if (time > 0) {
				// the furnace consumes fuel every tick,  we consume fuel every 4 ticks
				time /= 4;
				this.fuel += time;
				this.fuelQuality = time;
				this.needsFuel = false;
				return;
			}
		}
		this.fuelQuality = 0;
	}

	private AlloyTank getAlloyTank() {
		if(!active) {
			return null;
		}
		if(alloyTank != null) {
			return alloyTank;
		}

		// fetch all tanks
		List<FluidTankAnimated> newTanks = Lists.newLinkedList();
		for (BlockPos pos : tanks) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileTank) {
				newTanks.add(((TileTank)te).getInternalTank());
			} else if(te instanceof TileMelter) {
				newTanks.add(((TileMelter)te).getTank());
			}
		}
		if(!newTanks.isEmpty()) {
			alloyTank = new AlloyTank(newTanks);
		}

		return alloyTank;
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			// only allow extracting
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankWrapper);
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Validates the alloy tank structure
	 */
	public void checkTanks() {
		// client does not need anything more
		if(world.isRemote) {
			return;
		}

		// if powered or not an alloy tank, just clear active and exit
		if((this.getBlockMetadata() & 8) > 0 || !(this.getBlockType() instanceof BlockAlloyTank)) {
			makeInactive();
			return;
		}

		// set active flag if this combo is valid
		BlockAlloyTank tankBlock = (BlockAlloyTank)this.blockType;
		if(!tankBlock.isHeater(world.getBlockState(pos.down()))) {
			makeInactive();
			return;
		}

		if (!active) {
			active = true;
			// mark for update so the block model updates
			IBlockState state = world.getBlockState(pos);
			this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
		}

		// get a list of adjecent tanks
		BlockPos offset;
		tanks.clear();
		for(EnumFacing side : EnumFacing.VALUES) {
			// bottom holds the heater, no tank there
			if(side == EnumFacing.DOWN) {
				continue;
			}
			offset = pos.offset(side);

			// we only support seared tanks and melters for the tank
			// reduces exploits and keeps the smeltery feel
			IBlockState state = world.getBlockState(offset);
			if(tankBlock.isTank(state)) {
				tanks.add(offset);
			}
		}

		// remake alloy tank
		alloyTank = null;
	}

	private void makeInactive() {
		// if it was active, make it not so
		if(active) {
			// mark for update so the block model updates
			IBlockState state = world.getBlockState(pos);
			this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			active = false;
			tanks.clear();
			alloyTank = null;
		}
	}

	@Override
	public boolean hasFuel() {
		return fuel > 0;
	}

	/* GUI */

	@SideOnly(Side.CLIENT)
	@Override
	public float getFuelPercentage() {
		return (float)fuel / (float)fuelQuality;
	}

	@Override
	public int getFuel() {
		return fuel;
	}

	@Override
	public int getFuelQuality() {
		return fuelQuality;
	}

	@Override
	public void updateFuelFromPacket(int index, int fuel) {
		if(index == 0) {
			this.fuel = fuel;
		} else if(index == 1) {
			this.fuelQuality = fuel;
		}
	}

	/* NBT */

	private static final String TAG_FUEL = "fuel";
	private static final String TAG_FUEL_QUALITY = "fuel_quality";
	private static final String TAG_NEEDS_FUEL = "needs_fuel";
	private static final String TAG_TANKS = "tanks";
	private static final String TAG_ACTIVE = "active";
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tags) {
		tags = super.writeToNBT(tags);
		tags.setBoolean(TAG_ACTIVE, active);
		tags.setBoolean(TAG_NEEDS_FUEL, needsFuel);
		tags.setInteger(TAG_FUEL, fuel);
		tags.setInteger(TAG_FUEL_QUALITY, fuelQuality);

		NBTTagList tankList = new NBTTagList();
		for(BlockPos pos : tanks) {
			tankList.appendTag(TagUtil.writePos(pos));
		}
		tags.setTag(TAG_TANKS, tankList);

		return tags;
	}

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		active = tags.getBoolean(TAG_ACTIVE);
		needsFuel = tags.getBoolean(TAG_NEEDS_FUEL);
		fuel = tags.getInteger(TAG_FUEL);
		fuelQuality = tags.getInteger(TAG_FUEL_QUALITY);

		// write tanks to NBT
		NBTTagList tankList = tags.getTagList(TAG_TANKS, 10);
		tanks.clear();
		for(int i = 0; i < tankList.tagCount(); i++) {
			tanks.add(TagUtil.readPos(tankList.getCompoundTagAt(i)));
		}
	}
}
