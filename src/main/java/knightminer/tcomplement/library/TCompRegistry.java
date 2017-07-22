package knightminer.tcomplement.library;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

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
		meltingOverrides.add(recipe);
	}

	public static void registerMelterBlacklist(IBlacklist blacklist) {
		meltingBlacklist.add(blacklist);
	}

	public static void registerMelterBlacklist(RecipeMatch blacklist) {
		meltingBlacklist.add(new RecipeMatchBlacklist(blacklist));
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
