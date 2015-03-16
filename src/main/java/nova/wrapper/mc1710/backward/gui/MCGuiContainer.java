package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;

import nova.core.gui.AbstractGuiContainer;
import nova.core.gui.GuiComponent;
import nova.core.gui.Outline;
import nova.core.gui.nativeimpl.NativeContainer;
import nova.core.gui.render.Graphics;

public class MCGuiContainer extends MCGuiComponent<AbstractGuiContainer<?, ?>> implements NativeContainer {

	protected List<GuiComponent<?, ?>> components = new ArrayList<>();

	public MCGuiContainer(AbstractGuiContainer<?, ?> component) {
		super(component);
	}

	@Override
	public void addElement(GuiComponent<?, ?> element) {
		components.add(element);
	}

	@Override
	public void removeElement(GuiComponent<?, ?> element) {
		components.remove(element);
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		for (GuiComponent<?, ?> component : components) {
			Outline outline = component.getOutline();
			graphics.getCanvas().translate(outline.x1i(), outline.y1i());
			((DrawableGuiComponent) component.getNative()).draw(mouseX, mouseY, partial, graphics);
			graphics.getCanvas().translate(-outline.x1i(), -outline.y1i());
		}
		super.draw(mouseX, mouseY, partial, graphics);
	}
}
