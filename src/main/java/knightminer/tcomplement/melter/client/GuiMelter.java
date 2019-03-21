package knightminer.tcomplement.melter.client;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import knightminer.tcomplement.library.Util;
import knightminer.tcomplement.melter.inventory.ContainerMelter;
import knightminer.tcomplement.melter.tileentity.TileMelter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.client.GuiHeatingStructureFuelTank;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;

public class GuiMelter extends GuiHeatingStructureFuelTank implements IGuiLiquidTank {

	public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/melter.png");
	public static final ResourceLocation BACKGROUND_SOLID = Util.getResource("textures/gui/melter_solid.png");


	protected GuiElement scala = new GuiElement(176, 0, 52, 52, 256, 256);
	protected GuiElement progressBar = new GuiElementScalable(176, 150, 3, 16, 256, 256);
	protected GuiElement unprogressBar = new GuiElementScalable(179, 150, 3, 16);
	protected GuiElement uberHeatBar = new GuiElementScalable(182, 150, 3, 16);
	protected GuiElement noMeltBar = new GuiElementScalable(185, 150, 3, 16);
	protected GuiElement flame = new GuiElementScalable(176, 52, 14, 14);

	public TileMelter melter;
	private boolean solidFuel;
	private ResourceLocation background;

	public GuiMelter(ContainerMelter container, TileMelter melter) {
		super(container);

		this.melter = melter;
		this.background = BACKGROUND;

		// for the heater, adjust a few things
		this.solidFuel = melter.getSolidHeater() != null;
		if(solidFuel) {
			this.background = BACKGROUND_SOLID;
		}
	}

	// this is the same for both structures, but the superclass does not have (nor need) access to the side inventory
	@Override
	public void updateScreen() {
		super.updateScreen();

		// if the solif fuel status changes
		if((melter.getSolidHeater() != null) != solidFuel) {
			// close screen
			this.mc.player.closeScreen();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// we don't need to add the corner since the mouse is already reletive to the corner
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// draw the scale
		this.mc.getTextureManager().bindTexture(background);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		scala.draw(90, 16);

		// draw the tooltips, if any
		// subtract the corner of the main module so the mouse location is relative to just the center, rather than the side inventory
		mouseX -= cornerX;
		mouseY -= cornerY;

		// slot status display
		RenderHelper.disableStandardItemLighting();
		String tooltipText = null;

		// draw the "heat" bars for each slot
		if(this.inventorySlots instanceof ContainerMelter) {
			// skip the fourth slot if it exists
			for(Slot slot : ((ContainerMelter)this.inventorySlots).getInventorySlots()) {
				if(slot.getHasStack()) {
					float progress = melter.getHeatingProgress(slot.getSlotIndex());
					String tooltip = null;
					GuiElement bar = progressBar;

					if(Float.isNaN(progress)) {
						progress = 1f;
						bar = noMeltBar;
						tooltip = "gui.smeltery.progress.no_recipe";
					}
					else if(progress < 0) {
						bar = unprogressBar;
						progress = 1f;
						tooltip = "gui.smeltery.progress.no_heat";
					}
					else if((progress > 1f && progress < 2f) || progress == Float.POSITIVE_INFINITY) {
						progress = 1f;
					}
					else if(progress >= 2f && progress < 3f) {
						bar = uberHeatBar;
						progress = 1f;
						tooltip = "gui.tcomplement.melter.progress.no_space";
					}
					else if(progress >= 3f) {
						bar = uberHeatBar;
						progress = 1f;
						tooltip = "gui.tcomplement.melter.progress.wrong_fluid";
					}

					int height = 1 + Math.round(progress * (bar.h - 1));
					int x = slot.xPos - 4;
					int y = slot.yPos + bar.h - height;

					if(tooltip != null &&
							x <= mouseX && x + bar.w > mouseX &&
							y <= mouseY && y + bar.h > mouseY) {
						tooltipText = tooltip;
					}

					GuiScreen.drawModalRectWithCustomSizedTexture(x, y, bar.x, bar.y + bar.h - height, bar.w, height, bar.texW, bar.texH);
				}
			}
		}

		if(tooltipText != null) {
			drawHoveringText(this.fontRenderer.listFormattedStringToWidth(Util.translate(tooltipText), 100), mouseX, mouseY);
		}

		drawTankTooltip(mouseX, mouseY);
		// adjust coords based on fuel type
		int xMin = 153;
		int xMax = solidFuel ? 167 : 165;
		int yMin = solidFuel ? 36 : 16;
		int yMax = solidFuel ? 50 : 68;

		// Fuel tank rendering
		if(xMin <= mouseX && mouseX < xMax && yMin <= mouseY && mouseY < yMax) {
			if(melter.currentFuel != null) {
				drawFuelTooltip(mouseX, mouseY);
			}
			else {
				drawSolidFuelTooltip(mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(background);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		// draw liquids
		drawTank(90, 16, 52, 52);

		// update fuel info
		fuelInfo = melter.getFuelDisplay();
		if(solidFuel) {
			float fuel = melter.getFuelPercentage();
			if(fuel > 0) {
				this.mc.getTextureManager().bindTexture(background);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GuiElement flame = this.flame;
				int height = 1 + Math.round(fuel * (flame.h - 1));
				int x = 153 + cornerX;
				int y = 36 + cornerY + flame.h - height;

				GuiScreen.drawModalRectWithCustomSizedTexture(x, y, flame.x, flame.y + flame.h - height, flame.w, height, flame.texW, flame.texH);
			}
		} else {
			drawFuel(153, 16, 12, 52);
		}
	}

	protected FluidStack getHoveredFluidStack(int x, int y) {
		FluidTankAnimated tank = melter.getTank();
		FluidStack fluid = tank.getFluid();
		if(fluid != null && fluid.amount > 0) {
			int h = 52 - (52 * fluid.amount / tank.getCapacity());
			// within fluid
			if(x >= 90 && x < 142 && y >= (h + 16) && y < 68) {
				return fluid;
			}
		}
		return null;
	}

	// used by JEI, factor in the modules
	@Override
	public FluidStack getFluidStackAtPosition(int x, int y) {
		return getHoveredFluidStack(x - cornerX, y - cornerY);
	}

	/**
	 * Draws the fluid at the specified location
	 *
	 * @param displayX Display X location, excluding cornerX
	 * @param displayY Display Y location, excluding cornerY
	 * @param width    Width
	 * @param height   Height of the whole area, note that displayed size may differ due to how full the fuel is
	 */
	protected void drawTank(int displayX, int displayY, int width, int height) {
		FluidTankAnimated tank = melter.getTank();
		FluidStack fluid = tank.getFluid();
		if(fluid != null && fluid.amount > 0) {
			int x = displayX + cornerX;
			int y = displayY + cornerY + height;
			int w = width;
			int h = height * fluid.amount / tank.getCapacity();

			GuiUtil.renderTiledFluid(x, y - h, w, h, this.zLevel, fluid);
		}
	}

	/**
	 * Draws the fluid tooltip at the specified location
	 *
	 * @param x        Tooltip X location
	 * @param y        Tooltip Y location
	 * @param width    Width
	 * @param height   Height of the whole area, note that displayed size may differ due to how full the fuel is
	 */
	protected void drawTankTooltip(int x, int y) {
		List<String> tooltip = getTankTooltip(x, y);
		if(tooltip != null) {
			this.drawHoveringText(tooltip, x, y);
		}
	}

	/**
	 * Called by drawTankTooltip to generate the full tank tooltip
	 *
	 * @param x Tooltip X location
	 * @param y Tooltip Y location
	 * @return  List of the strings for the tooltip
	 */
	protected List<String> getTankTooltip(int x, int y) {
		if(x >= 90 && x < 142 && y >= 16 && y < 68) {
			FluidTankAnimated tank = melter.getTank();
			FluidStack hovered = getHoveredFluidStack(x, y);

			List<String> text = Lists.newArrayList();
			Consumer<Integer> stringFn = Util.isShiftKeyDown() ? (i) -> GuiUtil.amountToString(i, text) : (i) -> GuiUtil.amountToIngotString(i, text);

			if(hovered == null) {
				int usedCap = tank.getFluidAmount();
				int maxCap = tank.getCapacity();
				text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.capacity"));
				stringFn.accept(maxCap);
				text.add(Util.translateFormatted("gui.smeltery.capacity_available"));
				stringFn.accept(maxCap - usedCap);
				text.add(Util.translateFormatted("gui.smeltery.capacity_used"));
				stringFn.accept(usedCap);
				if(!Util.isShiftKeyDown()) {
					text.add("");
					text.add(Util.translate("tooltip.tank.holdShift"));
				}
			}
			else {
				text.add(TextFormatting.WHITE + hovered.getLocalizedName());
				GuiUtil.liquidToString(hovered, text);
			}

			return text;
		}

		return null;
	}

	/**
	 * Draws the tooltip for solid fuel
	 * @param mouseX  X position of the mouse
	 * @param mouseY  Y position of the mouse
	 */
	protected void drawSolidFuelTooltip(int mouseX, int mouseY) {
		List<String> text = Lists.newArrayList();
		text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.fuel"));
		if(melter.hasFuel()) {
			text.add(Util.translate("gui.tcomplement.melter.solid_fuel"));
			text.add(TextFormatting.GRAY + Util.translateFormatted("gui.tcomplement.melter.solid_fuel.amount", melter.getFuel() / 5));
			// melter stores temperature in Celsius, but utils use Kelvin
			text.add(Util.translateFormatted("gui.smeltery.fuel.heat", Util.celsiusString(melter.getTemperature())));
		}
		else {
			text.add(Util.translate("gui.smeltery.fuel.empty"));
		}
		this.drawHoveringText(text, mouseX, mouseY);
	}
}
