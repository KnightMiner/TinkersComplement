package knightminer.tcomplement.shared;

import static knightminer.tcomplement.shared.CommonsModule.steelIngot;
import static knightminer.tcomplement.shared.CommonsModule.steelNugget;
import static knightminer.tcomplement.steelworks.SteelworksModule.charcoalBlock;
import static knightminer.tcomplement.steelworks.SteelworksModule.steelBlock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.pulsar.pulse.Pulse;

/**
 * oredicts ALL the things in TComplement.
 * Conveniently gathered in one place!
 */
@Pulse(id = OredictModule.PulseId, forced = true)
public class OredictModule {
	public static final String PulseId = "Oredict";

	/**
	 * Registers all the blocks and item oredicts.
	 * Note that it's using the item registry event, since it's called after blocks.
	 * This relies on the oredict pulse being called after the pulses registering the items
	 */
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		oredict(charcoalBlock, "blockCharcoal");
		oredictNIB(steelNugget, steelIngot, steelBlock, "Steel");
	}

	/* Helper functions */
	private static void oredict(ItemStack stack, String... names) {
		if(stack != null && !stack.isEmpty()) {
			for(String name : names) {
				OreDictionary.registerOre(name, stack);
			}
		}
	}

	private static void oredictNIB(ItemStack nugget, ItemStack ingot, ItemStack block, String oreSuffix) {
		oredict(nugget, "nugget" + oreSuffix);
		oredict(ingot, "ingot" + oreSuffix);
		oredict(block, "block" + oreSuffix);
	}
}
