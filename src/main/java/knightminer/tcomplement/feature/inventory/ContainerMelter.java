package knightminer.tcomplement.feature.inventory;

import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerMelter extends ContainerMultiModule<TileMelter>{

	protected int[] oldHeats;
	private Slot[] slots;

	public ContainerMelter(InventoryPlayer inventoryPlayer, TileMelter tile) {
		super(tile);

		slots = new Slot[3];
		for(int i = 0; i < 3; i++) {
			SlotItemHandler slot = new SlotItemHandler(itemHandler, i, 22, 16 + i * 18);
			this.addSlotToContainer(slot);
			slots[i] = slot;
		}

		addPlayerInventory(inventoryPlayer, 8, 84);
		oldHeats = new int[tile.getSizeInventory()];
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		for(int i = 0; i < oldHeats.length; i++) {
			listener.sendWindowProperty(this, i, tile.getTemperature(i));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		// send changed heats
		for(int i = 0; i < oldHeats.length; i++) {
			int temp = tile.getTemperature(i);
			if(temp != oldHeats[i]) {
				oldHeats[i] = temp;
				for(IContainerListener crafter : this.listeners) {
					crafter.sendWindowProperty(this, i, temp);
				}
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		// id = index of the melting progress to update
		// data = temperature

		tile.updateTemperatureFromPacket(id, data);
	}

	public Slot[] getInventorySlots() {
		return slots;
	}

}
