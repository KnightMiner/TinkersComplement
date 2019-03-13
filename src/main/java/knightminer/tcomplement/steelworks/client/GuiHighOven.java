package knightminer.tcomplement.steelworks.client;

import java.io.IOException;
import java.util.List;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.steelworks.inventory.ContainerHighOven;
import knightminer.tcomplement.steelworks.inventory.ContainerHighOvenSideInventory;
import knightminer.tcomplement.steelworks.tileentity.TileHighOven;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;

public class GuiHighOven extends GuiMultiModule implements IGuiLiquidTank {

	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/high_oven.png");
	protected GuiElement scala = new GuiElement(176, 14, 35, 52, 256, 256);
	protected GuiElement flame = new GuiElementScalable(176, 0, 14, 14, 256, 256);

	protected final GuiHighOvenSideInventory sideinventory;
	protected final TileHighOven highOven;

	public GuiHighOven(ContainerHighOven container, TileHighOven highOven) {
		super(container);

		this.highOven = highOven;
		sideinventory = new GuiHighOvenSideInventory(this, container.getSubContainer(ContainerHighOvenSideInventory.class), highOven, highOven.getSizeInventory());
		addModule(sideinventory);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		// high oven size changed
		if(highOven.getSizeInventory() != sideinventory.inventorySlots.inventorySlots.size()) {
			// close screen
			this.mc.player.closeScreen();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// we don't need to add the corner since the mouse is already relative to the corner
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// draw the tooltips, if any
		// subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
		mouseX -= cornerX;
		mouseY -= cornerY;

		// Liquids
		List<String> tooltip = GuiUtil.getTankTooltip(highOven.getTank(), mouseX, mouseY, 133, 16, 168, 68);
		if(tooltip != null) {
			this.drawHoveringText(tooltip, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// draw liquids
		// TODO: why do I need that extra 17 on cornerY?
		GuiUtil.drawGuiTank(highOven.getTank(), 133 + cornerX, 16 + cornerY + 17, scala.w, scala.h, this.zLevel);

		// draw the scala
		this.mc.getTextureManager().bindTexture(BACKGROUND);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		scala.draw(133 + cornerX, 16 + cornerY);

		// draw the flame, shows how much fuel is left of the last consumed item
		float fuel = highOven.getFuelPercentage();
		if(fuel > 0) {
			GuiElement flame = this.flame;
			int height = 1 + Math.round(fuel * (flame.h - 1));
			int x = 80 + cornerX;
			int y = 35 + cornerY + flame.h - height;
			GuiScreen.drawModalRectWithCustomSizedTexture(x, y, flame.x, flame.y + flame.h - height, flame.w, height, flame.texW, flame.texH);
		}

		// temperature
		String temp = (highOven.getTemperature() - 300) + "\u00B0c";
		fontRenderer.drawString(temp, guiLeft + 88 - ((fontRenderer.getStringWidth(temp) / 2)), guiTop + 20, getTempColor());
	}

	/** Calculates the temperature text color based on temperature, pulled from Tinkers' Steelworks */
	protected int getTempColor() {
		int tempHex = highOven.getTemperature() - 300;
		if (tempHex > 2000) return 0xFF0000;

		// shift the temperature to have a gradient from 0 -> 1980 (which will visually give 20 -> 2000)
		float percent = (tempHex - 20) / 1980F;

		// 0xFF0000 <- 0x404040
		int r = (int) ((0xFF - 0x40) * percent) + 0x40;
		int gb = (int) ((0x00 - 0x40) * percent) + 0x40;

		return r << 16 | gb << 8 | gb;
	}

	@Override
	public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
		return GuiUtil.getFluidStackAtPosition(highOven.getTank(), mouseX - cornerX, mouseY - cornerY, 133, 16, 168, 68);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(mouseButton == 0) {
			GuiUtil.handleTankClick(highOven.getTank(), mouseX - cornerX, mouseY - cornerY, 133, 16, 168, 68);
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
