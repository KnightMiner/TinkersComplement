package knightminer.tcomplement.melter.inventory;

import javax.annotation.Nullable;

import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.melter.tileentity.TileHeater;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerHeater extends ContainerMultiModule<TileHeater> {

	private IHeaterConsumer consumer;
	private int oldFuel, oldFuelQuality;
	public ContainerHeater(InventoryPlayer inventoryPlayer, TileHeater tileHeater) {
		super(tileHeater);

		this.addSlotToContainer(new SlotHeaterFuel(tileHeater.getItemHandler(), 0, 80, 52));

		addPlayerInventory(inventoryPlayer, 8, 84);

		// add the heater if one exists
		TileEntity te = tileHeater.getWorld().getTileEntity(tileHeater.getPos().up());
		if(te instanceof IHeaterConsumer) {
			consumer = (IHeaterConsumer)te;
		}
	}

	/**
	 * Gets the consumer tied to this heater
	 * @return  consumer if present, null otherwise
	 */
	@Nullable
	public IHeaterConsumer getConsumer() {
		return consumer;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		if(consumer == null) {
			return;
		}

		listener.sendWindowProperty(this, 0, consumer.getFuel());
		listener.sendWindowProperty(this, 1, consumer.getFuelQuality());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if(consumer == null) {
			return;
		}

		// changed fuel data
		int fuel = consumer.getFuel();
		if(fuel != oldFuel) {
			oldFuel = fuel;
			sendUpdate(0, fuel);
		}
		fuel = consumer.getFuelQuality();
		if(fuel != oldFuelQuality) {
			oldFuelQuality = fuel;
			sendUpdate(1, fuel);
		}
	}

	private void sendUpdate(int index, int update) {
		for(IContainerListener crafter : this.listeners) {
			crafter.sendWindowProperty(this, index, update);
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		if(consumer != null) {
			consumer.updateFuelFromPacket(id, data);
		}
	}
}
