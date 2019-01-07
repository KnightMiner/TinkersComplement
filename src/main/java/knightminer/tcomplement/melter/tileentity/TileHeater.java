package knightminer.tcomplement.melter.tileentity;

import javax.annotation.Nonnull;

import knightminer.tcomplement.melter.client.GuiHeater;
import knightminer.tcomplement.melter.inventory.ContainerHeater;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileHeater extends TileInventory implements IInventoryGui {

	public TileHeater() {
		super("gui.tcomplement.heater.name", 1, 64);
	}

	/**
	 * Consumes a single piece of fuel in the heater
	 * @return  item burn time
	 */
	public int consumeFuel() {
		ItemStack stack = getStackInSlot(0);
		if(stack.isEmpty()) {
			return 0;
		}

		ItemStack fuel = stack.copy();
		int time = TileEntityFurnace.getItemBurnTime(fuel);
		if(time > 0) {
			fuel.shrink(1);
			// if the stack is now empty, return the container
			if(fuel.isEmpty()) {
				fuel = stack.getItem().getContainerItem(fuel);
			}

			// set the heater fuel
			this.setInventorySlotContents(0, fuel);
		}

		return time;
	}

	/* GUI */

	@Override
	public ContainerHeater createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerHeater(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiHeater(createContainer(inventoryplayer, world, pos), this);
	}

	public boolean isActive() {
		return isStackInSlot(0);
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
		// check if the slot is filled
		boolean wasFilled = this.isStackInSlot(0);
		super.setInventorySlotContents(slot, itemstack);

		// if its status changed, notify a block update
		if(wasFilled != this.isStackInSlot(0) && world != null) {
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 2);
		}
	}

	/* Sycing */
	// we send all our info to the client on load
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		readFromNBT(tag);
	}
}
