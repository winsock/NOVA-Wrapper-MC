package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import nova.core.gui.components.inventory.PlayerInventory;
import nova.core.gui.nativeimpl.NativePlayerInventory;
import nova.core.util.transform.Vector2i;
import nova.wrapper.mc1710.backward.gui.MCGui.MCContainer;
import nova.wrapper.mc1710.backward.inventory.BWInventory;

public class MCGuiPlayerInventory extends MCGuiComponent<PlayerInventory> implements NativePlayerInventory {

	List<Slot> slots = new ArrayList<>();

	public MCGuiPlayerInventory(PlayerInventory component) {
		super(component);
	}

	@Override
	public Optional<Vector2i> getPreferredSize() {
		return Optional.of(new Vector2i(0, 0));
	}

	@Override
	public void onAddedToContainer(MCContainer container) {
		IInventory inventory = ((BWInventory) getComponent().getInventory()).mcInventory;

	}
}
