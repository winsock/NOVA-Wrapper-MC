package nova.wrapper.mc1710.backward.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import nova.core.gui.Spacing;
import nova.core.gui.render.Canvas;
import nova.core.render.texture.Texture;
import nova.core.util.transform.Vector2i;

import org.lwjgl.opengl.GL11;

public class MCCanvas extends Canvas {

	private final Tessellator tessellator;

	public MCCanvas(int width, int height, Tessellator tessellator) {
		super(new Vector2i(width, height), false);
		this.tessellator = tessellator;
	}

	public double tx() {
		return state.tx;
	}

	public double ty() {
		return state.ty;
	}

	@Override
	public void bindTexture(Texture texture) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(texture.getResource()));
	}

	@Override
	public void rotate(double angle) {
		GL11.glRotated(angle, 0, 0, 1);
		super.rotate(angle);
	}

	@Override
	public void startDrawing(boolean textured) {
		if (!textured)
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glColor4f(state.color.redf(), state.color.greenf(), state.color.bluef(), state.color.alphaf());
		tessellator.startDrawing(GL11.GL_POLYGON);
	}

	@Override
	public void addVertex(double x, double y) {
		tessellator.addVertex(x + state.tx, y + state.ty, state.zIndex);
	}

	@Override
	public void addVertexWithUV(double x, double y, double u, double v) {
		tessellator.addVertexWithUV(x + state.tx, y + state.ty, state.zIndex, u, v);
	}

	@Override
	public void draw() {
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	@Override
	public void pop() {
		double angle = state.angle;
		super.pop();
		double newAngle = state.angle;
		if (angle != newAngle) {
			GL11.glRotated(newAngle - angle, 0, 0, 1);
		}
		Spacing scissor = state.scissor;
		GL11.glScissor(scissor.top(), scissor.right(), scissor.bottom(), scissor.left());
	}

	@Override
	public void setScissor(int top, int right, int bottom, int left) {
		GL11.glScissor(top, right, bottom, left);
		super.setScissor(top, right, bottom, left);
	}

	@Override
	public void enableScissor() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		super.enableScissor();
	}

	@Override
	public void disableScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		super.disableScissor();
	}
}
