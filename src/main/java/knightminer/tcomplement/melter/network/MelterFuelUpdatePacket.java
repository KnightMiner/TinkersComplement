package knightminer.tcomplement.melter.network;

import io.netty.buffer.ByteBuf;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class MelterFuelUpdatePacket extends AbstractPacketThreadsafe {

	public BlockPos pos;
	public int temperature;

	public MelterFuelUpdatePacket() {
	}

	public MelterFuelUpdatePacket(BlockPos pos, int temperature) {
		this.pos = pos;
		this.temperature = temperature;
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
		if(te instanceof TileMelter) {
			TileMelter melter = ((TileMelter) te);
			melter.updateTemperatureFromPacket(temperature);
			melter.currentFuel = null;
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
		temperature = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writePos(pos, buf);
		buf.writeInt(temperature);
	}
}
