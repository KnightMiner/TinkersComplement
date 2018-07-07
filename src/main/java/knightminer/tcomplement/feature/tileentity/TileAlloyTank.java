package knightminer.tcomplement.feature.tileentity;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.blocks.BlockAlloyTank;
import knightminer.tcomplement.library.tanks.AlloyTank;
import knightminer.tcomplement.library.tanks.FluidHandlerDrainOnlyWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TileAlloyTank extends TileTank implements ITickable {

	protected static final int ALLOYING_PER_TICK = 10;

	private boolean updateTanks;
	private AlloyTank alloyTank;
	private IFluidHandler tankWrapper;
	protected int tick;
	public TileAlloyTank() {
		super();
		this.tankWrapper = new FluidHandlerDrainOnlyWrapper(tank);
		updateTanks = true;
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

		// don't alloy if powered
		if((this.getBlockMetadata() & 8) > 0) {
			return;
		}

		// if we have not checked for tanks yet, do so
		if(updateTanks) {
			checkTanks();
		}

		// if we have no tanks, we are done right away
		if(alloyTank == null) {
			return;
		}

		// alloying time
		if(tick % 4 == 0) {
			// first, grab our fluid, exiting if full
			int amount = tank.getFluidAmount();
			if(amount == CAPACITY) {
				return;
			}

			// next, grab inputs, exiting if none
			List<FluidStack> fluids = alloyTank.getFluids();
			if(fluids.isEmpty()) {
				return;
			}

			// next, try all recipes
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
					break;
				}
			}
		}

		tick = (tick + 1) % 20;
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			// only allow extracting
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankWrapper);
		}
		return super.getCapability(capability, facing);
	}

	public void checkTanks() {
		if(world.isRemote) {
			return;
		}

		// needs to be an alloy tank so we have the block check callback
		Block block = world.getBlockState(pos).getBlock();
		if(!(block instanceof BlockAlloyTank)) {
			return;
		}
		BlockAlloyTank tankBlock = (BlockAlloyTank)block;

		// get a list of adjecent tanks
		// we don't store this in NBT since we would need to grab the fluid tanks again anyways
		BlockPos offset;
		List<FluidTankAnimated> newTanks = new LinkedList<>();
		for(EnumFacing side : EnumFacing.VALUES) {
			offset = pos.offset(side);

			// we only support seared tanks and melters for the tank
			// reduces exploits and keeps the smeltery feel
			IBlockState state = world.getBlockState(offset);
			if(tankBlock.isTank(state)) {
				// if we found one, set the side to true and try to find its fluid handler
				TileEntity te = world.getTileEntity(offset);
				if(te instanceof TileTank) {
					newTanks.add(((TileTank)te).getInternalTank());
				} else if(te instanceof TileMelter) {
					newTanks.add(((TileMelter)te).getTank());
				}
			}
		}
		// only define the tank if we have some
		alloyTank = newTanks.isEmpty() ? null : new AlloyTank(newTanks);
		updateTanks = false;
	}
}
