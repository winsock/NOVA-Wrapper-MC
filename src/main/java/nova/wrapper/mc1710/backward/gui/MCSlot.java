package nova.wrapper.mc1710.backward.gui;

import net.minecraft.inventory.IInventory;
import nova.core.gui.components.inventory.Slot;
import nova.core.gui.nativeimpl.NativeSlot;
import nova.wrapper.mc1710.backward.gui.MCGui.MCContainer;
import nova.wrapper.mc1710.backward.inventory.BWInventory;

public class MCSlot extends MCGuiComponent<Slot> implements NativeSlot, DrawableGuiComponent {

	private net.minecraft.inventory.Slot slot;

	public MCSlot(Slot component) {
		super(component);
	}

	@Override
	public void onAddedToContainer(MCContainer container) {
		IInventory inventory = ((BWInventory) getComponent().getInventory()).mcInventory;
		slot = new net.minecraft.inventory.Slot(inventory, getComponent().getSlotID(), 0, 0);
		container.inventorySlots.add(slot);
	}
}
