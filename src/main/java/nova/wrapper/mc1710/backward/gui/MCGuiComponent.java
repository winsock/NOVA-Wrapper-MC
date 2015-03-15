package nova.wrapper.mc1710.backward.gui;

import nova.core.gui.GuiComponent;
import nova.core.gui.Outline;
import nova.core.gui.nativeimpl.NativeGuiComponent;
import nova.core.gui.render.Graphics;

// TODO Keyboard and mouse interaction
public class MCGuiComponent implements NativeGuiComponent, DrawableGuiComponent {

	private final GuiComponent<?, ?> component;
	private Outline outline = Outline.empty;

	public MCGuiComponent(GuiComponent<?, ?> component) {
		this.component = component;
	}

	@Override
	public GuiComponent<?, ?> getComponent() {
		return component;
	}

	@Override
	public Outline getOutline() {
		return outline;
	}

	@Override
	public void setOutline(Outline outline) {
		this.outline = outline;
	}

	@Override
	public void requestRender() {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		getComponent().render(mouseX, mouseY, graphics);
	}
}
