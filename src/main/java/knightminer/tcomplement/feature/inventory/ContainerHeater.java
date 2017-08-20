package knightminer.tcomplement.feature.inventory;

import knightminer.tcomplement.feature.tileentity.TileHeater;
import net.minecraft.entity.player.InventoryPlayer;
import slimeknights.mantle.inventory.ContainerMultiModule;

public class ContainerHeater extends ContainerMultiModule<TileHeater> {

	public ContainerHeater(InventoryPlayer inventoryPlayer, TileHeater tileHeater) {
		super(tileHeater);

		this.addSlotToContainer(new SlotHeaterFuel(tileHeater.getItemHandler(), 0, 80, 52));

		addPlayerInventory(inventoryPlayer, 8, 84);
	}
}
