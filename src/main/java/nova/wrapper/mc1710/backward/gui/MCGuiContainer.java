package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;

import nova.core.gui.AbstractGuiContainer;
import nova.core.gui.GuiComponent;
import nova.core.gui.nativeimpl.NativeContainer;
import nova.core.gui.render.Graphics;

public class MCGuiContainer extends MCGuiComponent implements NativeContainer {
	
	private List<GuiComponent<?, ?>> components = new ArrayList<>();
	
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
		components.forEach((component) -> ((DrawableGuiComponent)component.getNative()).draw(mouseX, mouseY, partial, graphics));
		super.draw(mouseX, mouseY, partial, graphics);
	}
}
