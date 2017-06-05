package knightminer.knightsconstruct.library;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class KnightsRegistry {
	public static final Logger log = Util.getLogger("API");

	/*---------------------------------------------------------------------------
	| CREATIVE TABS                                                             |
	---------------------------------------------------------------------------*/
	public static CreativeTab tabGeneral = new CreativeTab("KnightsGeneral", new ItemStack(Items.IRON_SWORD));

	/*---------------------------------------------------------------------------
	| Melter                                                                    |
	---------------------------------------------------------------------------*/
	// this is basically a wrapper for the Tinkers Registry allowing me to override recipes without affecting the smeltery
	private static List<MeltingRecipe> meltingOverrides = Lists.newLinkedList();

	public static void registerMeltingOverride(MeltingRecipe recipe) {
		if(new TinkerRegisterEvent.MeltingRegisterEvent(recipe).fire()) {
			meltingOverrides.add(recipe);
		}
		else {
			try {
				String input = recipe.input.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
				log.debug("Registration of melting recipe for " + recipe.getResult().getUnlocalizedName() + " from " + input + " has been cancelled by event");
			} catch(Exception e) {
				log.error("Error when logging melting event", e);
			}
		}
	}

	public static MeltingRecipe getMelting(ItemStack stack) {
		// check if the recipe exists in our overrides
		for(MeltingRecipe recipe : meltingOverrides) {
			if(recipe.matches(stack)) {
				return recipe;
			}
		}

		// if not, use the Tinkers version
		return TinkerRegistry.getMelting(stack);
	}
}
