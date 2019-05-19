package knightminer.tcomplement.plugin.toolleveling;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.toolleveling.ModToolLeveling;

@Pulse(id = ToolLevelingPlugin.pulseID, description = "Add a Tinkers version of the Chisel Chisel", modsRequired = "tinkertoolleveling")
public class ToolLevelingPlugin {
    public static final String pulseID = "ToolLevelingPlugin";

    /**
     * Function to add XP to a given tool. Can be safely called as a NOOP when tool leveling is not loaded
     */
    public static XPInterface xpAdder = (tool, amount, player) -> {};

    @Subscribe
    public void init(FMLInitializationEvent event) {
        // find the addXp method and replace the NOOP version
        IModifier modifier = TinkerRegistry.getModifier("toolleveling");
        if (modifier instanceof ModToolLeveling) {
            xpAdder = ((ModToolLeveling) modifier)::addXp;
        }
    }

    /**
     * Interface to make the optional dependency easier, as that method can then be called without needing to reference Tinkers Tool Leveling
     */
    public interface XPInterface {
        /**
         * Adds XP to a given tool
         *
         * @param tool   Tool item stack
         * @param amount Amount of XP to add
         * @param player Player using the tool
         */
        void addXp(ItemStack tool, int amount, EntityPlayer player);
    }
}
