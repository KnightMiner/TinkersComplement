package knightminer.tcomplement.feature.client;

import javax.annotation.Nonnull;

import knightminer.tcomplement.feature.tileentity.TileMelter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;

public class MelterRenderer extends TileEntitySpecialRenderer<TileMelter> {

	protected static Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void renderTileEntityAt(@Nonnull TileMelter tile, double x, double y, double z, float partialTicks, int destroyStage) {
		FluidTankAnimated tank = tile.getTank();
		FluidStack liquid = tank.getFluid();

		if(liquid != null) {

			float height = (liquid.amount - tank.renderOffset) / tank.getCapacity() / 2;

			if(tank.renderOffset > 1.2f || tank.renderOffset < -1.2f) {
				tank.renderOffset -= (tank.renderOffset / 12f + 0.1f) * partialTicks;
			}
			else {
				tank.renderOffset = 0;
			}

			float d = RenderUtil.FLUID_OFFSET;
			RenderUtil.renderFluidCuboid(liquid, tile.getPos(), x, y + 0.5, z, d, d, d, 1d - d, height - d, 1d - d);
		}
	}
}
