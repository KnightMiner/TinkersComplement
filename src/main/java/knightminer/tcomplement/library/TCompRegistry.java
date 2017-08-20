package knightminer.tcomplement.library;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import knightminer.tcomplement.library.events.TCompRegisterEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class TCompRegistry {
	public static final Logger log = Util.getLogger("API");

	/*---------------------------------------------------------------------------
	| CREATIVE TABS                                                             |
	---------------------------------------------------------------------------*/
	public static CreativeTab tabGeneral = new CreativeTab("TCompGeneral", new ItemStack(Items.IRON_SWORD));

	/*---------------------------------------------------------------------------
	| Melter                                                                    |
	---------------------------------------------------------------------------*/
	// this is basically a wrapper for the Tinkers Registry allowing me to override recipes without affecting the smeltery
	private static List<MeltingRecipe> meltingOverrides = Lists.newLinkedList();

	private static List<IBlacklist> meltingBlacklist = Lists.newLinkedList();

	public static void registerMelterOverride(MeltingRecipe recipe) {
		if(new TCompRegisterEvent.MelterOverrideRegisterEvent(recipe).fire()) {
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

	public static void registerMelterBlacklist(IBlacklist blacklist) {
		if(new TCompRegisterEvent.MelterBlackListRegisterEvent(blacklist).fire()) {
			meltingBlacklist.add(blacklist);
		}
		else {
			try {
				log.debug("Registration of melter blacklist recipe has been cancelled by event");
			} catch(Exception e) {
				log.error("Error when logging melting event", e);
			}
		}
	}

	public static void registerMelterBlacklist(RecipeMatch blacklist) {
		registerMelterBlacklist(new RecipeMatchBlacklist(blacklist));
	}

	public static MeltingRecipe getMelting(ItemStack stack) {
		// check if the recipe exists in our overrides
		for(MeltingRecipe recipe : meltingOverrides) {
			if(recipe.matches(stack)) {
				return recipe;
			}
		}
		// if not, check if it is a blacklisted melting recipe
		for(IBlacklist blacklist : meltingBlacklist) {
			if(blacklist.matches(stack)) {
				return null;
			}
		}

		// if not, use the Tinkers version
		return TinkerRegistry.getMelting(stack);
	}
}
