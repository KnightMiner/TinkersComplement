package knightminer.tcomplement.shared.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.SectionData;

@SideOnly(Side.CLIENT)
public class ModuleSectionData extends SectionData {
	public String module = "";
}
