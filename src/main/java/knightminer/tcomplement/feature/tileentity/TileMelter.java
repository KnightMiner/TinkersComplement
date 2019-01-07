package knightminer.tcomplement.feature.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.tcomplement.common.TCompNetwork;
import knightminer.tcomplement.feature.client.GuiMelter;
import knightminer.tcomplement.feature.inventory.ContainerMelter;
import knightminer.tcomplement.feature.multiblock.MultiblockMelter;
import knightminer.tcomplement.feature.network.MelterFuelUpdatePacket;
import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.tanks.FluidHandlerDrainOnlyWrapper;
import knightminer.tcomplement.library.tanks.MelterTank;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.network.HeatingStructureFuelUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TileMelter extends TileHeatingStructureFuelTank<MultiblockMelter> implements ITickable, IInventoryGui, IHeaterConsumer {

	private int tick;

	// liquid stored inside
	private FluidTankAnimated tank;
	private IFluidHandler tankWrapper;

	protected static final int CAPACITY = Material.VALUE_Ingot * 16;

	public TileMelter() {
		super("gui.tcomplement.melter.name", 3, 1);

		setMultiblock(new MultiblockMelter(this));
		tank = new MelterTank(CAPACITY, this);
		tankWrapper = new FluidHandlerDrainOnlyWrapper(tank);
	}

	public FluidTankAnimated getTank() {
		return tank;
	}

	public boolean isSolidFuel() {
		return hasFuel() && currentFuel == null;
	}

	public TileHeater getSolidHeater() {
		TileEntity te = world.getTileEntity(pos.down());
		if(te instanceof TileHeater) {
			return (TileHeater) te;
		}
		return null;
	}

	@Override
	public void update() {
		if(isClientWorld()) {
			return;
		}

		// are we fully formed?
		if(!isActive()) {
			if(tick % 20 == 0) {
				checkMultiblockStructure();
			}
		} else {
			if(tick % 4 == 0) {
				heatItems();
			}

			if(needsFuel) {
				consumeFuel();
			}
		}

		tick = (tick + 1) % 20;
	}

	@Override
	protected void consumeFuel() {
		// no need to consume fuel
		if(hasFuel()) {
			return;
		}

		// consume fuel!
		// catch world being null as this is called during loading
		World world = getWorld();
		if(world == null) {
			return;
		}
		currentTank = this.pos.down();
		TileEntity te = world.getTileEntity(currentTank);
		if(te instanceof TileTank) {
			IFluidTank tank = ((TileTank) te).getInternalTank();

			FluidStack liquid = tank.getFluid();
			if(liquid != null) {
				FluidStack in = liquid.copy();
				int bonusFuel = TinkerRegistry.consumeSmelteryFuel(in);
				int amount = liquid.amount - in.amount;
				FluidStack drained = tank.drain(amount, false);

				// we can drain. actually drain and add the fuel
				if(drained != null && drained.amount == amount) {
					tank.drain(amount, true);
					currentFuel = drained.copy();
					fuelQuality = bonusFuel;
					addFuel(bonusFuel, drained.getFluid().getTemperature(drained) - 300); // convert to degree celcius

					// notify client of fuel/temperature changes
					if(isServerWorld()) {
						TinkerNetwork.sendToAll(new HeatingStructureFuelUpdatePacket(pos, currentTank, temperature, currentFuel));
					}

					return;
				}
			}

			fuelQuality = 0;
		} else if (te instanceof TileHeater) {
			int time = ((TileHeater)te).consumeFuel();
			if (time > 0) {
				time /= 2;
				currentFuel = null;
				fuelQuality = time;

				// just about enough to melt clay or most metals, but not iron
				// also, about the temperature of a conventional oven I guess
				addFuel(time, 200);

				// notify client of fuel/temperature changes
				if(isServerWorld()) {
					TCompNetwork.sendToAll(new MelterFuelUpdatePacket(pos, temperature));
				}
			} else {
				fuelQuality = 0;
			}
		}
	}

	@Override
	protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
		if(structure.blocks.size() < 2) {
			return;
		}

		tanks.clear();
		BlockPos down = pos.down();
		if(getWorld().getBlockState(down).getBlock() instanceof BlockTank) {
			// find all tanks for input
			tanks.add(down);
			if(isSolidFuel()) {
				this.fuel = 0;
				this.temperature = 0;
				this.needsFuel = true;
			}
		}

		this.resize(3);
	}

	@Override
	protected int getUpdatedInventorySize(int width, int height, int depth) {
		return 3;
	}

	/* Smeltery logic */
	@Override
	protected void updateHeatRequired(int index) {
		ItemStack stack = getStackInSlot(index);
		if(!stack.isEmpty()) {
			MeltingRecipe melting = TCompRegistry.getMelting(stack);
			if(melting != null) {
				setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));
				// instantly consume fuel if required
				if(!hasFuel()) {
					consumeFuel();
				}

				return;
			}
		}

		setHeatRequiredForSlot(index, 0);
	}

	// melt stuff
	@Override
	protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
		MeltingRecipe recipe = TCompRegistry.getMelting(stack);

		if(recipe == null) {
			return false;
		}

		// fluid is different
		FluidStack fluid = recipe.output.copy();
		FluidStack current = getTank().getFluid();
		if(current != null && current.getFluid() != fluid.getFluid()) {
			itemTemperatures[slot] = itemTempRequired[slot] * 3 + 1;
			return false;
		}

		// unfortunately, the event requires a smeltery, so not fired
		//TinkerSmelteryEvent.OnMelting event = TinkerSmelteryEvent.OnMelting.fireEvent(this, stack, recipe.output.copy());

		int filled = getTank().fill(fluid.copy(), false);

		if(filled == fluid.amount) {
			getTank().fill(fluid, true);

			// only clear out items n stuff if it was successful
			setInventorySlotContents(slot, ItemStack.EMPTY);
			return true;
		}
		else {
			// can't fill into the melter, set error state
			itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
		}

		return false;
	}

	/* Fluid interactions */
	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			// only allow extraction
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankWrapper);
		}
		return super.getCapability(capability, facing);
	}

	/* Client sync */

	// called only clientside to sync with the server
	@SideOnly(Side.CLIENT)
	public void updateFluidTo(FluidStack fluid) {
		int oldAmount = tank.getFluidAmount();
		tank.setFluid(fluid);

		tank.renderOffset += tank.getFluidAmount() - oldAmount;
	}

	/* GUI */

	@Override
	public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerMelter(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiMelter((ContainerMelter)createContainer(inventoryplayer, world, pos), this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FuelInfo getFuelDisplay() {
		FuelInfo info = new FuelInfo();

		// just return the current contents of the tank below, don't worry about last fluid
		if(hasFuel() && currentFuel != null) {
			info.fluid = currentFuel.copy();
			info.heat = this.temperature + 300;
			info.maxCap = currentFuel.amount;
		}

		IFluidTank tank = getTankAt(pos.down());
		if(tank != null) {
			FluidStack fuel = tank.getFluid();
			if(fuel != null) {
				if (info.fluid == null) {
					info.fluid = fuel.copy();
					info.heat = info.fluid.getFluid().getTemperature(info.fluid);
					info.maxCap = tank.getCapacity();
				} else if(tank.getFluid().isFluidEqual(info.fluid)) {
					info.fluid.amount += tank.getFluidAmount();
					info.maxCap += tank.getCapacity();
				}
			}
		}

		return info;
	}

	@Override
	public int getFuelQuality() {
		return fuelQuality;
	}

	/**
	 * Grabs the tank at the given location (if present)
	 */
	private IFluidTank getTankAt(BlockPos pos) {
		TileEntity te = getWorld().getTileEntity(pos);
		if(te instanceof TileTank) {
			return ((TileTank) te).getInternalTank();
		}

		return null;
	}

	/* NBT */

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		getTank().writeToNBT(compound);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		getTank().readFromNBT(compound);
	}
}