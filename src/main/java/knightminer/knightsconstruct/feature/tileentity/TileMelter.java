package knightminer.knightsconstruct.feature.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.knightsconstruct.feature.client.GuiMelter;
import knightminer.knightsconstruct.feature.inventory.ContainerMelter;
import knightminer.knightsconstruct.feature.multiblock.MultiblockMelter;
import knightminer.knightsconstruct.library.KnightsRegistry;
import knightminer.knightsconstruct.library.feature.MelterTank;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank;

public class TileMelter extends TileHeatingStructureFuelTank<MultiblockMelter> implements ITickable, IInventoryGui {

	private int tick;

	// liquid stored inside
	private FluidTankAnimated tank;

	protected static final int CAPACITY = Material.VALUE_Ingot * 16;

	public TileMelter() {
		super("gui.melter.name", 3, 1);

		setMultiblock(new MultiblockMelter(this));
		tank = new MelterTank(CAPACITY, this);
	}

	public FluidTankAnimated getTank() {
		return tank;
	}

	@Override
	public void update() {
		if(isClientWorld()) {
			return;
		}

		// are we fully formed?
		if(!isActive()) {
			checkMultiblockStructure();
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
	protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
		tanks.clear();
		BlockPos down = pos.down();
		if(getWorld().getBlockState(down).getBlock() instanceof BlockTank) {
			// find all tanks for input
			tanks.add(down);
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
			MeltingRecipe melting = KnightsRegistry.getMelting(stack);
			if(melting != null) {
				FluidStack current = tank.getFluid();
				if(current == null || current.getFluid() == melting.getResult().getFluid()) {
					setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));

					// instantly consume fuel if required
					if(!hasFuel()) {
						consumeFuel();
					}
				} else {
					setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));
				}

				return;
			}
		}

		setHeatRequiredForSlot(index, 0);
	}

	// melt stuff
	@Override
	protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
		MeltingRecipe recipe = KnightsRegistry.getMelting(stack);

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
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
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