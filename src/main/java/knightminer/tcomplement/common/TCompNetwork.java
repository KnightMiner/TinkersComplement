package knightminer.tcomplement.common;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.feature.network.FluidUpdatePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.common.TinkerNetwork;

public class TCompNetwork extends NetworkWrapper {

	public static TCompNetwork instance = new TCompNetwork();

	public TCompNetwork() {
		super(TinkersComplement.modID);
	}

	public void setup() {
		// register all the packets

		// MELTER
		registerPacketClient(FluidUpdatePacket.class);
	}

	public static void sendToAll(AbstractPacket packet) {
		instance.network.sendToAll(packet);
	}

	public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		for(EntityPlayer player : world.playerEntities) {
			// only send to relevant players
			if(!(player instanceof EntityPlayerMP)) {
				continue;
			}
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			if(world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.x, chunk.z)) {
				TinkerNetwork.sendTo(packet, playerMP);
			}
		}
	}
}
