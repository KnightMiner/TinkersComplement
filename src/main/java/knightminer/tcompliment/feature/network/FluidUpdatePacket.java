package knightminer.tcompliment.feature.network;

import io.netty.buffer.ByteBuf;
import knightminer.tcompliment.feature.tileentity.TileMelter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class FluidUpdatePacket extends AbstractPacketThreadsafe {

	public BlockPos pos;
	public FluidStack fluid;

	public FluidUpdatePacket() {
	}

	public FluidUpdatePacket(BlockPos pos, FluidStack fluid) {
		this.pos = pos;
		this.fluid = fluid;
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
		if(te instanceof TileMelter) {
			((TileMelter) te).updateFluidTo(fluid);
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		// clientside only
		throw new UnsupportedOperationException("Serverside only");
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = readPos(buf);
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		fluid = FluidStack.loadFluidStackFromNBT(tag);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writePos(pos, buf);
		NBTTagCompound tag = new NBTTagCompound();
		if(fluid != null) {
			fluid.writeToNBT(tag);
		}
		ByteBufUtils.writeTag(buf, tag);
	}
}
