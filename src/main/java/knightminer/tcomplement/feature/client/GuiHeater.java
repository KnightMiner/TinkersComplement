package knightminer.tcomplement.feature.client;

import java.util.List;

import com.google.common.collect.Lists;

import knightminer.tcomplement.feature.inventory.ContainerHeater;
import knightminer.tcomplement.feature.tileentity.TileHeater;
import knightminer.tcomplement.library.IHeaterConsumer;
import knightminer.tcomplement.library.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class GuiHeater extends GuiMultiModule {

	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/heater.png");

	protected GuiElement flame = new GuiElementScalable(176, 0, 14, 14);

	public IHeaterConsumer consumer;
	public GuiHeater(ContainerHeater container, TileHeater heater) {
		super(container);
		this.consumer = container.getConsumer();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// if we have a melter, draw the fuel
		if(consumer != null) {
			float fuel = consumer.getFuelPercentage();

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

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// draw the tooltips, if any
		// subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
		mouseX -= cornerX;
		mouseY -= cornerY;

		// fuel tooltip rendering
		if(consumer != null && 81 <= mouseX && mouseX < 95 && 36 <= mouseY && mouseY < 50) {
			drawSolidFuelTooltip(mouseX, mouseY);
		}
	}

	/**
	 * Draws the tooltip for solid fuel
	 * @param mouseX  X position of the mouse
	 * @param mouseY  Y position of the mouse
	 */
	protected void drawSolidFuelTooltip(int mouseX, int mouseY) {
		if(consumer.hasFuel()) {
			List<String> text = Lists.newArrayList();
			text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.fuel"));
			text.add(TextFormatting.GRAY + Util.translateFormatted("gui.tcomplement.melter.solid_fuel.amount", consumer.getFuel() / 5));
			this.drawHoveringText(text, mouseX, mouseY);
		}
	}

}
