package knightminer.tcomplement.shared.legacy;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import knightminer.tcomplement.TinkersComplement;
import knightminer.tcomplement.library.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class TileEntityRenamer implements IFixableData {

	public static final Set<String> NAMES_TO_UPDATE = ImmutableSet.copyOf(new String[] {
			"heater",
			"melter"
	});
	public static final String PREFIX = "minecraft:" + TinkersComplement.modID + ".";
	public static final int PREFIX_LEN = PREFIX.length();

	@Override
	public int getFixVersion() {
		return 1;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound tags) {
		String id = tags.getString("id");
		// all bad names start with "minecraft:ceramics."
		if(id.startsWith(PREFIX)) {
			// so check if its one of ours after the prefix
			id = id.substring(PREFIX_LEN);
			if(NAMES_TO_UPDATE.contains(id)) {
				// if so, remap
				tags.setString("id", Util.resource(id));
			}
		}
		return tags;
	}

}
