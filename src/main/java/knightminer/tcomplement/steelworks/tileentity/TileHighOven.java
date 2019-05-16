package knightminer.tcomplement.steelworks.tileentity;

import java.util.List;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.library.steelworks.IHeatRecipe;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.steelworks.client.GuiHighOven;
import knightminer.tcomplement.steelworks.inventory.ContainerHighOven;
import knightminer.tcomplement.steelworks.inventory.InventoryHighOven;
import knightminer.tcomplement.steelworks.multiblock.MultiblockHighOven;
import knightminer.tcomplement.steelworks.tank.HighOvenTank;
import knightminer.tcomplement.steelworks.tank.HighOvenTank.FilterMatchType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;

public class TileHighOven extends TileHeatingStructure<MultiblockHighOven> implements ITickable, IInventoryGui, ISmelteryTankHandler {

	/** Temperature of the high oven when not heated in celsius */
	private static final int ROOM_TEMPERATURE = 20;
	/** Degrees lost per second without fuel */
	private static final int COOLDOWN_RATE = 10;
	/** Degrees added to the max temperature per layer */
	private static final int DEGREES_PER_LAYER = 500;
	/** Base temperature cap at 0 layers */
	private static final int BASE_TEMPERATURE_CAP = 1500;
	/** Maximum temperature cap for the melter, corresponds to 16 layers */
	private static final int MAX_TEMPERATURE_CAP = 9500;
	/** Maximum number of item slots in the melter */
	private static final int MAX_SLOTS = 7;

	private static final int CAPACITY_PER_LAYER = Material.VALUE_Ingot * 12;

	protected HighOvenTank liquids;
	protected InventoryHighOven additives;
	protected int tick;

	// properties
	protected int maxTemperature = 0;
	protected int fuelRate;
	public int fuelQuality;

	// local properties
	protected IItemHandlerModifiable combinedItemHandler;
	protected IHeatRecipe heatRecipeCache;
	protected IMixRecipe mixRecipeCache;

	public TileHighOven() {
		super("gui.tcomplement.high_oven.name", 0, 1);
		setMultiblock(new MultiblockHighOven(this));
		liquids = new HighOvenTank(this);
		additives = new InventoryHighOven(this);
		combinedItemHandler = new CombinedInvWrapper(additives, this.getItemHandler());
	}

	@Override
	public void update() {
		if(isClientWorld()) {
			return;
		}

		// are we fully formed?
		if(!isActive()) {
			// check for furnace once per second
			if(tick == 0) {
				checkMultiblockStructure();
				// rapidly cool down the structure
				if(!active) {
					this.temperature = Math.max((this.temperature / 2) - COOLDOWN_RATE, ROOM_TEMPERATURE);
				}
			}
		}
		else {
			if(tick % 4 == 0) {
				// prevent method from changing fuel
				// TODO: maybe separate variable?
				int fuel = this.fuel;
				heatItems();
				this.fuel = fuel;
			}

			if(tick % 5 == 0) {
				heatFluid();
			}

			// kill entities inside
			if(tick == 0) {
				interactWithEntitiesInside();
				updateTemperature();
			}

			// we don't check the inside for obstructions since it should not be
			// possible unless the outside was modified
		}

		tick = (tick + 1) % 20;
	}

	/* Heating structure logic */

	@Override
	public boolean hasFuel() {
		// in high oven land, you don't need fuel to cook, just heat
		return temperature > ROOM_TEMPERATURE;
	}

	@Override
	protected void updateHeatRequired(int index) {
		ItemStack stack = getStackInSlot(index);
		if(!stack.isEmpty()) {
			MeltingRecipe melting = TCompRegistry.getOvenMelting(stack);
			if(melting != null) {
				setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));
				return;
			}
		}

		setHeatRequiredForSlot(index, 0);
	}

	@Override
	protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
		MeltingRecipe recipe = TCompRegistry.getOvenMelting(stack);
		if(recipe == null) {
			return false;
		}

		// got our recipe result, check if the result can be put in the tank
		FluidStack meltingOutput = FluidUtil.getValidFluidStackOrNull(recipe.output.copy());
		FilterMatchType type = liquids.matchesFilter(meltingOutput, true);
		if(type == FilterMatchType.NONE) {
			// fluid not accepted, set error state
			itemTemperatures[slot] = itemTempRequired[slot] * 3 + 1;
			return false;
		}

		// input means we are allowed to try a mix recipe
		if(type == FilterMatchType.INPUT) {
			// first, try the cached recipe
			ItemStack oxidizer = additives.getAdditive(MixAdditive.OXIDIZER);
			ItemStack reducer = additives.getAdditive(MixAdditive.REDUCER);
			ItemStack purifier = additives.getAdditive(MixAdditive.PURIFIER);
			IMixRecipe mixRecipe;
			if(mixRecipeCache != null && mixRecipeCache.matches(meltingOutput, oxidizer, reducer, purifier)) {
				mixRecipe = mixRecipeCache;
			} else {
				// note we don't pass in temperature here so the cache is not invalided by wrong temperature
				// recipes must be unique by input combination, temperature is just an extra parameters
				mixRecipe = TCompRegistry.getMixRecipe(meltingOutput, oxidizer, reducer, purifier);
				// found a recipe? cache it
				if(mixRecipe != null) {
					mixRecipeCache = mixRecipe;
				}
			}

			// if we have a recipe, apply it
			if (mixRecipe != null) {
				// if the temperature is too small, this will just return the input
				FluidStack mixOutput = mixRecipe.getOutput(meltingOutput, temperature);
				int filled = liquids.fillInternal(mixOutput, false);
				if(filled == mixOutput.amount) {
					liquids.fillInternal(mixOutput, true);

					// only clear out items n stuff if it was successful
					setInventorySlotContents(slot, ItemStack.EMPTY);
					// this method does nothing if the temperature is too low
					mixRecipe.updateAdditives(meltingOutput, oxidizer, reducer, purifier, temperature);
					// TODO: set back into additives?
					return true;
				} else {
					// can't fill into the high oven, set error state
					itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
					return false;
				}
			}
		}

		// either we did not find a mix recipe, or hit the filter on output
		int filled = liquids.fillInternal(meltingOutput, false);
		if(filled == meltingOutput.amount) {
			liquids.fillInternal(meltingOutput, true);

			// only clear out items n stuff if it was successful
			setInventorySlotContents(slot, ItemStack.EMPTY);
			return true;
		}

		// can't fill into the high oven, set error state
		itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
		return false;
	}

	@Override
	protected void consumeFuel() {
		ItemStack fuelStack = additives.getFuel();
		if(fuelStack.isEmpty()) {
			return;
		}
		HighOvenFuel fuel = TCompRegistry.getHighOvenFuel(fuelStack);
		if(fuel != null) {
			fuelStack.shrink(1);
			this.fuel += fuel.getTime();
			this.fuelQuality = fuel.getTime();
			this.fuelRate = fuel.getRate();
			this.needsFuel = false;
		}
	}

	protected void heatFluid() {
		FluidStack current = liquids.getFilterFluid();
		if(current != null) {
			IHeatRecipe recipe = heatRecipeCache;
			if(recipe == null || !recipe.matches(current)) {
				recipe = TCompRegistry.getHeatRecipe(current);
			}

			if(recipe == null) {
				return;
			}

			// check how many times we match, this also handles temperature checks
			heatRecipeCache = recipe;
			int matches = recipe.timesMatched(current, temperature);
			if(matches <= 0) {
				return;
			}

			// ensure we have space if the output is bigger
			FluidStack input = recipe.getInput();
			FluidStack output = recipe.getOutput();

			// sizes are different, ensure we have space
			if(output.amount > input.amount) {
				int available = liquids.getCapacity() - liquids.getFluidAmount();
				matches = Math.min(matches, available / (output.amount - input.amount));
				if(matches == 0) {
					return;
				}
			}

			// update the tank fluids
			liquids.drainInternal(new FluidStack(input, input.amount * matches), true);
			liquids.fillInternal(new FluidStack(output, output.amount * matches), true);
			liquids.moveFluidToBottom(output);
		}
	}

	/* Run every second to update fuel consumed */
	protected void updateTemperature() {
		// if we have no fuel, burn some
		if(fuel <= 0) {
			consumeFuel();
		}

		// that may have given us some fuel, so check again
		if(fuel > 0) {
			temperature = Math.min(temperature + fuelRate, maxTemperature);
			fuel--;
		} else if(temperature != ROOM_TEMPERATURE) {
			temperature = Math.max(temperature - COOLDOWN_RATE, ROOM_TEMPERATURE);
		}
	}

	/* Multiblock logic */

	@Override
	protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
		int inventorySize = Math.min(structure.yd, MAX_SLOTS);

		// if the new multiblock is smaller we pop out all items that don't fit
		// in anymore
		if(this.getSizeInventory() > inventorySize) {
			for(int i = inventorySize; i < getSizeInventory(); i++) {
				if(!getStackInSlot(i).isEmpty()) {
					dropItem(getStackInSlot(i));
				}
			}
		}

		// adjust inventory, tank, and max temperature
		this.resize(inventorySize);
		this.liquids.setCapacity(structure.yd * CAPACITY_PER_LAYER);
		this.maxTemperature = Math.min(BASE_TEMPERATURE_CAP + structure.yd * DEGREES_PER_LAYER, MAX_TEMPERATURE_CAP);
	}

	protected void dropItem(ItemStack stack) {
		EnumFacing direction = getWorld().getBlockState(pos).getValue(BlockSearedFurnaceController.FACING);
		BlockPos pos = this.getPos().offset(direction);

		EntityItem entityitem = new EntityItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
		getWorld().spawnEntity(entityitem);
	}

	protected void interactWithEntitiesInside() {
		// find all monsters within the furnace and kill them
		AxisAlignedBB bb = info.getBoundingBox().contract(1, 1, 1).offset(0, 0.5, 0).expand(0, 0.5, 0);

		List<EntityLivingBase> entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, bb);

		for(EntityLivingBase entity : entities) {
			if(entity instanceof EntityMob && entity.isEntityAlive()) {
				entity.setDead();
			}
		}
	}

	/* Extra inventory logic */

	public IItemHandlerModifiable getAdditives() {
		return additives;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		// return false for sides so hoppers cannot interact, need to block previous hasCapability calls hence the tertiary
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? facing == null : super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		// if item handler, we say no to non-null sides
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return facing == null ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(combinedItemHandler) : null;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		ItemStack current = this.getStackInSlot(slot);
		super.setInventorySlotContents(slot, stack);
		if(!ItemStack.areItemStacksEqual(current, stack)) {
			this.markDirtyFast();
		}
	}

	/* Fluid logic */

	@Override
	public SmelteryTank getTank() {
		return liquids;
	}

	/* GUI */
	@Override
	public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerHighOven(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiHighOven((ContainerHighOven) createContainer(inventoryplayer, world, pos), this);
	}

	public float getHeatingProgress(int index) {
		if(index < 0 || index >= getSizeInventory()) {
			return -1f;
		}

		if(!canHeat(index)) {
			return -1f;
		}

		return getProgress(index);
	}

	/* Networking and NBT */

	@Override
	public void markDirtyFast() {
		if (this.world != null) {
			this.world.markChunkDirty(this.pos, this);
		}
	}

	@Override
	public void onTankChanged(List<FluidStack> fluids, FluidStack changed) {
		// notify clients of liquid changes
		if(isServerWorld()) {
			TinkerNetwork.sendToAll(new SmelteryFluidUpdatePacket(pos, fluids));
		}
		this.markDirtyFast();
	}

	/**
	 * Updates a GUI value from the packet
	 * @param index  0 for fuel, 1 for fuel quality, and 2 for temperature
	 * @param value  New value
	 */
	@SideOnly(Side.CLIENT)
	public void updateFromPacket(int index, int value) {
		if(index == 0) {
			this.fuel = value;
		} else if(index == 1) {
			this.fuelQuality = value;
		} else if(index == 2) {
			this.temperature = value;
		}
	}

	/**
	 * Can be used by the GUI to determine fuel percentage
	 */
	@SideOnly(Side.CLIENT)
	public float getFuelPercentage() {
		return (float) fuel / (float) fuelQuality;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateFluidsFromPacket(List<FluidStack> fluids) {
		this.liquids.setFluids(fluids);
	}

	private static final String TAG_MAX_TEMPERATURE = "maxTemperature";
	private static final String TAG_FUEL_QUALITY = "fuelQuality";
	private static final String TAG_FUEL_RATE = "fuelRate";
	private static final String TAG_ADDITIVES = "additives";

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		liquids.writeToNBT(compound);
		compound.setInteger(TAG_MAX_TEMPERATURE, maxTemperature);
		compound.setInteger(TAG_FUEL_QUALITY, fuelQuality);
		compound.setInteger(TAG_FUEL_RATE, fuelRate);
		compound.setTag(TAG_ADDITIVES, additives.serializeNBT());

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		liquids.readFromNBT(compound);
		maxTemperature = compound.getInteger(TAG_MAX_TEMPERATURE);
		fuelQuality = compound.getInteger(TAG_FUEL_QUALITY);
		fuelRate = compound.getInteger(TAG_FUEL_RATE);
		additives.deserializeNBT(compound.getCompoundTag(TAG_ADDITIVES));
	}
}
