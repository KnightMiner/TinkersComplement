package knightminer.tcomplement.feature.client;

import knightminer.tcomplement.feature.inventory.ContainerHeater;
import knightminer.tcomplement.feature.tileentity.TileHeater;
import knightminer.tcomplement.feature.tileentity.TileMelter;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class GuiHeater extends GuiMultiModule {

	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/heater.png");

	protected GuiElement flame = new GuiElementScalable(176, 0, 14, 14);

	public TileMelter melter;
	public GuiHeater(ContainerHeater container, TileHeater heater) {
		super(container);

		// add the heater if one exists
		TileEntity te = heater.getWorld().getTileEntity(heater.getPos().up());
		if(te instanceof TileMelter) {
			melter = (TileMelter)te;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// if we have a melter, draw the fuel
		if(melter != null) {
			float fuel = melter.getFuelPercentage();

			if(fuel > 0) {
				this.mc.getTextureManager().bindTexture(BACKGROUND);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GuiElement flame = this.flame;
				int height = 1 + Math.round(fuel * (flame.h - 1));
				int x = 81 + cornerX;
				int y = 36 + cornerY + flame.h - height;

				GuiScreen.drawModalRectWithCustomSizedTexture(x, y, flame.x, flame.y + flame.h - height, flame.w, height, flame.texW, flame.texH);
			}
		}
	}

}
