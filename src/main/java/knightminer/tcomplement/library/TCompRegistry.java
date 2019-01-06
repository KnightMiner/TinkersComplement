package knightminer.tcomplement.library;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import knightminer.tcomplement.library.events.TCompRegisterEvent;
import net.minecraft.init.Blocks;
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
	public static CreativeTab tabGeneral = new CreativeTab("TCompGeneral", new ItemStack(Blocks.BRICK_BLOCK));
	public static CreativeTab tabTools = new CreativeTab("TCompTools", new ItemStack(Items.IRON_PICKAXE));

	/*---------------------------------------------------------------------------
	| Melter                                                                    |
	---------------------------------------------------------------------------*/
	// this is basically a wrapper for the Tinkers Registry allowing me to override recipes without affecting the smeltery
	private static List<MeltingRecipe> meltingOverrides = Lists.newLinkedList();

	private static List<IBlacklist> meltingBlacklist = Lists.newLinkedList();

	/**
	 * Registers a melter override recipe. This is a recipe that exists only in the melter, typically used to replace a smeltery recipe
	 * @param recipe  Recipe to register
	 */
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

	/**
	 * Gets all melter overrides
	 * @return  Immutable list of all melter overrides
	 */
	public static List<MeltingRecipe> getAllMeltingOverrides() {
		return ImmutableList.copyOf(meltingOverrides);
	}

	/**
	 * Blacklists an input from being used for a normal smeltery recipe. This is not needed if an override is added with that input
	 * @param blacklist  Blacklist entry
	 */
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

	/**
	 * Registers a blacklist entry using a RecipeMatch entry
	 * @param blacklist  RecipeMatch to blacklist
	 */
	public static void registerMelterBlacklist(RecipeMatch blacklist) {
		registerMelterBlacklist(new RecipeMatchBlacklist(blacklist));
	}

	/**
	 * Checks if a melting recipe is hidden by the melter overrides or blacklist
	 * @param recipe  Recipe to check
	 * @return  true if the recipe would be hidden, false otherwise
	 */
	public static boolean isSmeltingHidden(MeltingRecipe recipe) {
		List<ItemStack> inputs = recipe.input.getInputs();

		// TODO: should probably validate that all inputs match for cases of list inputs, but probably not an issue
		// check blacklist first, its probably quicker
		for(IBlacklist blacklist : meltingBlacklist) {
			if(inputs.stream().anyMatch(blacklist::matches)) {
				return true;
			}
		}

		// next try overrides
		for(MeltingRecipe override : meltingOverrides) {
			if(inputs.stream().anyMatch(override::matches)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the melting recipe for a given item stack.
	 * This checks the overrides first, then runs though the blacklist before checking the smeltery registry
	 * @param stack  Input stack
	 * @return  recipe instance
	 */
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
