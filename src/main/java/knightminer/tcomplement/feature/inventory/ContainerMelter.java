package knightminer.tcomplement.feature.inventory;

import knightminer.tcomplement.feature.tileentity.TileHeater;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerMelter extends ContainerMultiModule<TileMelter>{

	protected int[] oldHeats;
	protected int inventorySize, oldFuel, oldFuelQuality;
	private Slot[] slots;

	public ContainerMelter(InventoryPlayer inventoryPlayer, TileMelter tile) {
		super(tile);

		// add the heater item slot if we have one. Mainly a convenience over its own GUI
		TileHeater heater = tile.getSolidHeater();
		if(heater != null) {
			this.addSlotToContainer(new SlotHeaterFuel(heater.getItemHandler(), 0, 152, 52));
		}

		slots = new Slot[3];
		for(int i = 0; i < 3; i++) {
			SlotItemHandler slot = new HeaterSlot(itemHandler, i, 22, 16 + i * 18);
			this.addSlotToContainer(slot);
			slots[i] = slot;
		}


		addPlayerInventory(inventoryPlayer, 8, 84);
		inventorySize = slots.length;
		oldHeats = new int[inventorySize];
		oldFuel = 0;
		oldFuelQuality = 0;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		listener.sendWindowProperty(this, 0, tile.getFuel());
		listener.sendWindowProperty(this, 1, tile.fuelQuality);

		for(int i = 0; i < inventorySize; i++) {
			listener.sendWindowProperty(this, i + 2, tile.getTemperature(i));
		}
	}


	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		// changed fuel data
		int fuel = tile.getFuel();
		if(fuel != oldFuel) {
			oldFuel = fuel;
			sendUpdate(0, fuel);
		}
		fuel = tile.fuelQuality;
		if(fuel != oldFuelQuality) {
			oldFuelQuality = fuel;
			sendUpdate(1, fuel);
		}

		// send changed heats
		for(int i = 0; i < inventorySize; i++) {
			int temp = tile.getTemperature(i);
			if(temp != oldHeats[i]) {
				oldHeats[i] = temp;
				sendUpdate(i + 2, temp);
			}
		}
	}

	private void sendUpdate(int index, int update) {
		for(IContainerListener crafter : this.listeners) {
			crafter.sendWindowProperty(this, index, update);
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		// first two indexes are fuel, specifically fuel and fuelQuality
		if(id < 2) {
			tile.updateFuelFromPacket(id, data);
		}
		// next is a set the size of the inventory of current temperatures
		else if(id < inventorySize + 2) {
			tile.updateTemperatureFromPacket(id - 2, data);
		}
	}

	public Slot[] getInventorySlots() {
		return slots;
	}

	private static class HeaterSlot extends SlotItemHandler {
		public HeaterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return true;
		}

		@Override
		public int getItemStackLimit(ItemStack stack) {
			return 1;
		}
	}
}
