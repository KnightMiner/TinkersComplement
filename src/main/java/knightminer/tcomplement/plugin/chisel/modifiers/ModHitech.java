package knightminer.tcomplement.plugin.chisel.modifiers;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.plugin.chisel.ChiselPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.modifiers.ToolModifier;
import team.chisel.common.util.NBTUtil;

public class ModHitech extends ToolModifier {
    private static final String CHISEL_TAG = "chiseldata";
    private static final String KEY_TARGET = "target";

    public ModHitech() {
        super("hitech", 0xD38947);

        addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.freeModifier, ChiselPlugin.CHISEL_ONLY);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {}

    @Override
    protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
        // disallow adding hitech if the chisel has a target item, as it would be lost
        if(hasTargetItem(stack)) {
            String error = Util.translate("gui.tcomplement.error.has_chisel_target");
            throw new TinkerGuiException(error);
        }
        return true;
    }

    private static boolean hasTargetItem(ItemStack stack) {
        // if missing chisel data, no target item
        NBTTagCompound tags = TagUtil.getTagSafe(stack);
        if(!tags.hasKey(CHISEL_TAG)) {
            return false;
        }

        // if no target tag, no target item
        tags = tags.getCompoundTag(CHISEL_TAG);
        if(!tags.hasKey(KEY_TARGET)) {
            return false;
        }

        // tag might exist and be set to air, its annoying
        return !(new ItemStack(tags.getCompoundTag(KEY_TARGET)).isEmpty());
    }
}
