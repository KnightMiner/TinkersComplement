package knightminer.tcomplement.library;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import knightminer.tcomplement.library.events.TCompRegisterEvent;
import knightminer.tcomplement.library.steelworks.EmptyMixRecipe;
import knightminer.tcomplement.library.steelworks.HighOvenFuel;
import knightminer.tcomplement.library.steelworks.IMixRecipe;
import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class TCompRegistry {
	public static final Logger log = Util.getLogger("API");

	/*-------------------------------------------------------------------------*\
	| CREATIVE TABS                                                             |
	\*-------------------------------------------------------------------------*/
	public static CreativeTab tabGeneral = new CreativeTab("TCompGeneral", new ItemStack(Blocks.BRICK_BLOCK));
	public static CreativeTab tabTools = new CreativeTab("TCompTools", new ItemStack(Items.IRON_PICKAXE));

	/*-------------------------------------------------------------------------*\
	| Melter                                                                    |
	\*-------------------------------------------------------------------------*/
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


	/*-------------------------------------------------------------------------*\
	| High Oven Mixes                                                           |
	\*-------------------------------------------------------------------------*/
	private static List<IMixRecipe> mixRegistry = Lists.newLinkedList();
	private static Map<MixAdditive,RecipeMatchRegistry> mixAdditives = new EnumMap<>(MixAdditive.class);
	static {
		for(MixAdditive type : MixAdditive.values()) {
			mixAdditives.put(type, new RecipeMatchRegistry());
		}
	}

	// TODO: probably want a resource location for this, to make CT support easier
	/**
	 * Registers a new high oven mix recipe
	 * @param recipe  Recipe to register
	 * @return  Registered recipe for chaining
	 */
	@Nonnull
	public static IMixRecipe registerMix(IMixRecipe recipe) {
		if(new TCompRegisterEvent.HighOvenMixRegisterEvent(recipe).fire()) {
			mixRegistry.add(recipe);
			return recipe;
		}
		else try {
			FluidStack output = recipe.getOutput();
			if (output != null) {
				log.debug("Registration of mix recipe for " + output.getUnlocalizedName() + " has been cancelled by event");
			}
		} catch(Exception e) {
			log.error("Error when logging mix event", e);
		}
		// empty recipe so no null checks needed for chaining
		return EmptyMixRecipe.INSTANCE;
	}

	/**
	 * Registers a new mix from the given input and output
	 * @param output  Recipe fluid output
	 * @param input   Recipe fluid input
	 * @return  IMixRecipe instance
	 */
	@Nonnull
	public static IMixRecipe registerMix(FluidStack output, FluidStack input) {
		return registerMix(new MixRecipe(output, input));
	}

	/**
	 * Gets the mix recipe for the given set of inputs
	 * @param fluid     FluidStack input
	 * @param oxidizer  Item in oxidizer slot
	 * @param reducer   Item in reducer slot
	 * @param purifier  Item in purifier slot
	 * @return  First mix recipe found, null if nothing found
	 */
	public static IMixRecipe getMixRecipe(FluidStack fluid, ItemStack oxidizer, ItemStack reducer, ItemStack purifier) {
		for(IMixRecipe recipe : mixRegistry) {
			if (recipe.matches(fluid, oxidizer, reducer, purifier)) {
				return recipe;
			}
		}
		return null;
	}

	/**
	 * Gets a mix recipe based on its input and output
	 * @param output  Fluid result
	 * @param input   Fluid input
	 * @return  First mix recipe found, null if nothing found
	 */
	public static IMixRecipe getMixRecipe(FluidStack output, FluidStack input) {
		for(IMixRecipe recipe : mixRegistry) {
			if (recipe.matches(output, input)) {
				return recipe;
			}
		}
		return null;
	}

	/**
	 * Registers a mix additive for the sake of the high oven slot checks
	 * @param additive  Additive recipe match to register
	 * @param type      Additive type
	 */
	public static void registerMixAdditive(@Nonnull RecipeMatch additive, @Nonnull MixAdditive type) {
		mixAdditives.get(type).addRecipeMatch(additive);
	}

	/**
	 * Checks if the given stack is a valid mix additive
	 * @param stack  Stack to check
	 * @param type   Additive type
	 * @return  True if a valid additive
	 */
	public static boolean isValidMixAdditive(@Nonnull ItemStack stack, @Nonnull MixAdditive type) {
		return mixAdditives.get(type).matches(stack).isPresent();
	}

	/*-------------------------------------------------------------------------*\
	| High Oven Fuels                                                           |
	\*-------------------------------------------------------------------------*/
	private static List<HighOvenFuel> highOvenFuels = Lists.newLinkedList();

	private static void registerFuel(HighOvenFuel fuel) {
		if(new TCompRegisterEvent.HighOvenFuelRegisterEvent(fuel).fire()) {
			highOvenFuels.add(fuel);
		}
		else try {
			String input = fuel.getFuels().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
			log.debug("Registration of high oven fuel for " + input + " has been cancelled by event");
		} catch(Exception e) {
			log.error("Error when logging high oven fuel event", e);
		}
	}

	/**
	 * Registers a new high oven fuel
	 * @param fuel  Fuel recipe match entry
	 * @param time  Fuel burn time in seconds
	 * @param rate  Temperature increase per second
	 */
	public static void registerFuel(RecipeMatch fuel, int time, int rate) {
		registerFuel(new HighOvenFuel(fuel, time, rate));
	}

	/**
	 * Registers a new high oven fuel
	 * @param fuel  Fuel item stack
	 * @param time  Fuel burn time in seconds
	 * @param rate  Temperature increase per second
	 */
	public static void registerFuel(ItemStack fuel, int time, int rate) {
		registerFuel(RecipeMatch.of(fuel), time, rate);
	}

	/**
	 * Registers a new high oven fuel
	 * @param fuel  Fuel oredictionary name
	 * @param time  Fuel burn time in seconds
	 * @param rate  Temperature increase per second
	 */
	public static void registerFuel(String fuel, int time, int rate) {
		registerFuel(RecipeMatch.of(fuel), time, rate);
	}

	/**
	 * Gets the HighOvenFuel object matching this item stack
	 * @param input  Stack to search
	 * @return  HighOvenFuel object containing time and rate information
	 */
	public static HighOvenFuel getHighOvenFuel(ItemStack input) {
		for (HighOvenFuel fuel : highOvenFuels) {
			if (fuel.matches(input)) {
				return fuel;
			}
		}
		return null;
	}

	/**
	 * Checks if the given fuel is valid
	 * @param input  Stack to search
	 * @return true if valid, false otherwise
	 */
	public static boolean isHighOvenFuel(ItemStack input) {
		return getHighOvenFuel(input) != null;
	}

	/*-------------------------------------------------------------------------*\
	| High Oven Overrides                                                       |
	\*-------------------------------------------------------------------------*/
	private static List<MeltingRecipe> highOvenOverrides = Lists.newLinkedList();

	/**
	 * Registers a melter override recipe. This is a recipe that exists only in the melter, typically used to replace a smeltery recipe
	 * @param recipe  Recipe to register
	 */
	public static void registerHighOvenOverride(MeltingRecipe recipe) {
		if(new TCompRegisterEvent.HighOvenOverrideRegisterEvent(recipe).fire()) {
			highOvenOverrides.add(recipe);
		}
		else try {
			String input = recipe.input.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
			log.debug("Registration of melting recipe for " + recipe.getResult().getUnlocalizedName() + " from " + input + " has been cancelled by event");
		} catch(Exception e) {
			log.error("Error when logging melting event", e);
		}
	}

	/**
	 * Gets all high oven overrides
	 * @return  Immutable list of all melter overrides
	 */
	public static List<MeltingRecipe> getAllHighOvenOverrides() {
		return ImmutableList.copyOf(highOvenOverrides);
	}

	/**
	 * Checks if a melting recipe is hidden by the melter overrides or blacklist
	 * @param recipe  Recipe to check
	 * @return  true if the recipe would be hidden, false otherwise
	 */
	public static boolean isOvenHidden(MeltingRecipe recipe) {
		List<ItemStack> inputs = recipe.input.getInputs();

		// TODO: should probably validate that all inputs match for cases of list inputs, but probably not an issue
		// no blacklist, we can just try overrides
		for(MeltingRecipe override : highOvenOverrides) {
			if(inputs.stream().anyMatch(override::matches)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the melting recipe for a given item stack for the high oven.
	 * This is the same as the TinkerRegistry method, but checks high oven overrides
	 * @param stack  Input stack
	 * @return  recipe instance
	 */
	public static MeltingRecipe getOvenMelting(ItemStack stack) {
		// check if the recipe exists in our overrides
		for(MeltingRecipe recipe : highOvenOverrides) {
			if(recipe.matches(stack)) {
				return recipe;
			}
		}

		// if not, use the Tinkers version
		return TinkerRegistry.getMelting(stack);
	}
}
