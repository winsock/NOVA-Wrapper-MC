package nova.wrapper.mc1710.backward.gui;

import nova.core.gui.AbstractGuiContainer;
import nova.core.gui.Gui;
import nova.core.gui.GuiComponent;
import nova.core.gui.components.Button;
import nova.core.gui.components.inventory.Slot;
import nova.core.gui.factory.GuiComponentFactory;
import nova.core.gui.nativeimpl.NativeButton;
import nova.core.gui.nativeimpl.NativeContainer;
import nova.core.gui.nativeimpl.NativeGui;
import nova.core.gui.nativeimpl.NativeGuiComponent;
import nova.core.gui.nativeimpl.NativeSlot;

public class MCGuiComponentFactory extends GuiComponentFactory {

	public MCGuiComponentFactory() {
		registerNativeComponent(NativeGui.class,
				component -> new MCGui((Gui) component));
		registerNativeComponent(NativeContainer.class,
				component -> new MCGuiContainer((AbstractGuiContainer<?, ?>) component));
		registerNativeComponent(NativeButton.class,
				component -> new MCButton((Button) component));
		registerNativeComponent(NativeGuiComponent.class,
				component -> new MCGuiComponent<GuiComponent<?, ?>>(component));
		registerNativeComponent(NativeSlot.class,
				component -> new MCGuiSlot((Slot) component));
	}
}
