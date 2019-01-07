package knightminer.tcomplement.feature.tileentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.blocks.BlockAlloyTank;
import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.library.tanks.AlloyTank;
import knightminer.tcomplement.library.tanks.FluidHandlerDrainOnlyWrapper;
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
	public TileAlloyTank() {
		super();
		tanks = Lists.newLinkedList();
		this.tankWrapper = new FluidHandlerDrainOnlyWrapper(tank);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
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
			int amount = tank.getFluidAmount();
			if(amount != CAPACITY) {
				// must have side tanks, though if we got this far we should
				AlloyTank alloyTank = getAlloyTank();
				if(alloyTank != null) {
					// must have fluids in those side tanks
					List<FluidStack> fluids = alloyTank.getFluids();
					if(!fluids.isEmpty()) {
						// finally, time to try recipes
						FluidStack current = tank.getFluid();
						for(AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
							// first, ensure the recipe works with our current fluid, if we have one
							FluidStack result = FluidUtil.getValidFluidStackOrNull(recipe.getResult());
							if(result == null || (current != null && !result.isFluidEqual(current))) {
								continue;
							}

							// find out how often we can apply the recipe
							int matched = recipe.matches(fluids);
							// if we matched at all, run further checks
							if(matched > 0) {
								// if we matched, ensure we have fuel
								if(!hasFuel()) {
									needsFuel = true;
									break;
								}

								matched = matched * result.amount;
								if(matched > ALLOYING_PER_TICK) {
									matched = ALLOYING_PER_TICK;
								}
								// ensure we do not alloy above what we can store total
								matched = Math.min(matched, CAPACITY - amount);
								do {
									// remove all liquids from the tank
									for(FluidStack liquid : recipe.getFluids()) {
										FluidStack toDrain = liquid.copy();
										FluidStack drained = alloyTank.drain(toDrain, true);
										// error logging
										assert drained != null;
										if(!drained.isFluidEqual(toDrain) || drained.amount != toDrain.amount) {
											TinkersComplement.log.error("Smeltery alloy creation drained incorrect amount: was {}:{}, should be {}:{}", drained
													.getUnlocalizedName(), drained.amount, toDrain.getUnlocalizedName(), toDrain.amount);
										}
									}

									// and insert the alloy
									FluidStack toFill = result.copy();
									int filled = tank.fill(toFill, true);
									if(filled != result.amount) {
										TinkersComplement.log.error("Smeltery alloy creation filled incorrect amount: was {}, should be {} ({})", filled,
												recipe.getResult().amount * matched, result.getUnlocalizedName());
									}
									matched -= filled;
								} while(matched > 0);
								fuel--;
								break;
							}
						}
					}
				}
			}
		}

		tick = (tick + 1) % 20;
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
		if(te instanceof TileHeater) {
			int time = ((TileHeater)te).consumeFuel();
			if (time > 0) {
				this.fuel += time;
				this.fuelQuality = time;
				this.needsFuel = false;
			} else {
				this.fuelQuality = 0;
			}
		}
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
		if(world.isRemote) {
			return;
		}

		// if powered, just clear active and exit
		if((this.getBlockMetadata() & 8) > 0) {
			active = false;
			tanks.clear();
			alloyTank = null;
			return;
		}

		// needs to be an alloy tank so we have the block check callback
		if(!(this.getBlockType() instanceof BlockAlloyTank)) {
			return;
		}
		BlockAlloyTank tankBlock = (BlockAlloyTank)this.blockType;

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

		// set active flag if this combo is valid
		active = tankBlock.isHeater(world.getBlockState(pos.down()));
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
