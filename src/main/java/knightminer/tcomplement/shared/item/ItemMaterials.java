package knightminer.tcomplement.shared.item;

import gnu.trove.procedure.TIntObjectProcedure;
import knightminer.tcomplement.common.Config;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.item.ItemMetaDynamic;

public class ItemMaterials extends ItemMetaDynamic {
	@Override
	@SideOnly(Side.CLIENT)
	protected void registerItemModels(final Item item) {
		names.forEachEntry(new TIntObjectProcedure<String>() {
			@Override
			public boolean execute(int meta, String name) {
				// old bucket texture
				if(Config.oldBucketTexture && name.equals("stone_bucket")) {
					name = "stone_bucket_old";
				}

				// tell the game which model to use for this item-meta combination
				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), name));
				return true;
			}
		});
	}
}
