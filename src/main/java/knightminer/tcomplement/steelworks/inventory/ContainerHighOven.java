package knightminer.tcomplement.steelworks.inventory;

import javax.annotation.Nonnull;

import knightminer.tcomplement.library.TCompRegistry;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerHighOven extends ContainerMultiModule<TileHighOven> {

	protected ContainerHighOvenSideInventory sideInventory;

	private Slot[] slots;
	private int[] oldHeats;
	private int oldFuel, oldFuelQuality, oldTemperature;
	public ContainerHighOven(InventoryPlayer inventoryPlayer, TileHighOven tile) {
		super(tile);

		// add slots
		slots = new Slot[4];
		slots[0] = new SlotFuel(itemHandler, 80, 52);
		this.addSlotToContainer(slots[0]);

		// additives
		int index;
		for(MixAdditive type : MixAdditive.values()) {
			index = type.getSlotIndex();
			slots[index+1] = new SlotAdditive(itemHandler, type, 9, 16 + index * 18);
			this.addSlotToContainer(slots[index+1]);
		}

		sideInventory = new ContainerHighOvenSideInventory(tile, 0, 0);
		addSubContainer(sideInventory, true);

		addPlayerInventory(inventoryPlayer, 8, 84);

		oldHeats = new int[tile.getSizeInventory()];
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		listener.sendWindowProperty(this, 0, tile.getFuel());
		listener.sendWindowProperty(this, 1, tile.fuelQuality);
		listener.sendWindowProperty(this, 2, tile.getTemperature());
		for(int i = 0; i < oldHeats.length; i++) {
			listener.sendWindowProperty(this, i+3, tile.getTemperature(i));
		}
	}

	private void sendUpdate(int index, int value) {
		for(IContainerListener crafter : this.listeners) {
			crafter.sendWindowProperty(this, index, value);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		// fuel, fuel quality, and temperature
		int value = tile.getFuel();
		if(value != oldFuel) {
			sendUpdate(0, value);
		}
		value = tile.fuelQuality;
		if(value != oldFuelQuality) {
			sendUpdate(1, value);
		}
		value = tile.getTemperature();
		if(value != oldTemperature) {
			sendUpdate(2, value);
		}

		// send changed heats
		for(int i = 0; i < oldHeats.length; i++) {
			value = tile.getTemperature(i);
			if(value != oldHeats[i]) {
				oldHeats[i] = value;
				sendUpdate(i+3, value);
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		// first three are fuel, fuelQuality, and temperature
		if(id < 3) {
			tile.updateFromPacket(id, data);
		} else {
			// remainder are slot fuels
			tile.updateTemperatureFromPacket(id-3, data);
		}
	}

	private static class SlotFuel extends SlotItemHandler {
		public SlotFuel(IItemHandler itemHandler, int xPosition, int yPosition) {
			super(itemHandler, InventoryHighOven.SLOT_FUEL, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(@Nonnull ItemStack stack) {
			if (stack.isEmpty()) {
				return false;
			}
			// this check is a bit easier than the additive check
			ItemStack currentStack = this.getStack();
			if (!currentStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, currentStack)) {
				return false;
			}
			return TCompRegistry.isHighOvenFuel(stack);
		}
	}

	private static class SlotAdditive extends SlotItemHandler {
		private MixAdditive type;
		public SlotAdditive(IItemHandler itemHandler, MixAdditive type, int xPosition, int yPosition) {
			super(itemHandler, type.getSlotIndex(), xPosition, yPosition);
			this.type = type;
		}

		@Override
		public boolean isItemValid(@Nonnull ItemStack stack) {
			if (stack.isEmpty()) {
				return false;
			}
			// this check is a bit easier than the additive check
			ItemStack currentStack = this.getStack();
			if (!currentStack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, currentStack)) {
				return false;
			}
			return TCompRegistry.isValidMixAdditive(stack, type);
		}
	}
}
