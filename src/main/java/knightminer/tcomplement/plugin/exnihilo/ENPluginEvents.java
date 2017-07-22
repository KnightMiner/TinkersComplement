package knightminer.tcomplement.plugin.exnihilo;

import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ENPluginEvents {
	// Extra width/height modifier management
	@SubscribeEvent
	public void onExtraBlockBreak(TinkerToolEvent.ExtraBlockBreak event) {
		if(TinkerModifiers.modHarvestWidth == null || TinkerModifiers.modHarvestHeight == null) {
			return;
		}

		NBTTagList modifiers = TagUtil.getBaseModifiersTagList(event.itemStack);
		boolean width = false;
		boolean height = false;
		for(int i = 0; i < modifiers.tagCount(); i++) {
			String modId = modifiers.getStringTagAt(i);
			if(modId.equals(TinkerModifiers.modHarvestWidth.getIdentifier())) {
				width = true;
			}
			else if(modId.equals(TinkerModifiers.modHarvestHeight.getIdentifier())) {
				height = true;
			}
		}

		if(!width && !height) {
			return;
		}

		if(event.tool == ExNihiloPlugin.sledgeHammer) {
			event.width += width ? 2 : 0;
			event.height += height ? 2 : 0;
		}
	}
}
