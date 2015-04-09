package nova.wrapper.mc1710.forward.block;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import nova.core.block.Block;
import nova.core.block.components.DynamicallyModelled;
import nova.core.block.components.Modelled;
import nova.core.util.transform.MatrixStack;
import nova.wrapper.mc1710.backward.render.BWModel;

/**
 * @author Calclavia
 */
public class FWTileRenderer extends TileEntitySpecialRenderer {

	public static final FWTileRenderer instance = new FWTileRenderer();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float p_147500_8_) {
		Block block = ((FWTile) tile).getBlock();
		if (block != null) {
			if (block instanceof DynamicallyModelled) {
				BWModel model = new BWModel();
				model.matrix = new MatrixStack().translate(x + 0.5, y + 0.5,
						z + 0.5).getMatrix();
				((DynamicallyModelled) block).renderDynamic(model);
				model.renderWorld(tile.getWorldObj());
			}
		}
	}
}
