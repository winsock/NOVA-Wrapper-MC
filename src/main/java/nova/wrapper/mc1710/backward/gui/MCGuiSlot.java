package nova.wrapper.mc1710.backward.gui;

import java.util.Optional;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import nova.core.gui.components.inventory.Slot;
import nova.core.gui.nativeimpl.NativeSlot;
import nova.core.gui.render.Graphics;
import nova.core.util.transform.Vector2i;
import nova.wrapper.mc1710.backward.gui.MCGui.MCContainer;
import nova.wrapper.mc1710.backward.inventory.BWInventory;

import org.lwjgl.opengl.GL11;

public class MCGuiSlot extends MCGuiComponent<Slot> implements NativeSlot, DrawableGuiComponent {

	private net.minecraft.inventory.Slot slot;

	public MCGuiSlot(Slot component) {
		super(component);
	}

	@Override
	public Optional<Vector2i> getPreferredSize() {
		return Optional.of(new Vector2i(16, 16));
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		GuiContainer gui = getGui().getGuiScreen();
		drawSlot(0, 0, gui, slot, mouseX, mouseY);
		super.draw(mouseX, mouseY, partial, graphics);
	}

	protected static void drawSlot(int x, int y, GuiContainer gui, net.minecraft.inventory.Slot slot, int mouseX, int mouseY) {
		slot.xDisplayPosition = x;
		slot.yDisplayPosition = y;
		// Translate item renderer back to the origin
		GL11.glTranslatef(0, 0, -150);
		gui.func_146977_a(slot);
		GL11.glTranslatef(0, 0, 150);

		if (gui.isMouseOverSlot(slot, mouseX, mouseY) && slot.func_111238_b()) {
			gui.theSlot = slot;
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glColorMask(true, true, true, false);
			gui.drawGradientRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	@Override
	public void onAddedToContainer(MCContainer container) {
		IInventory inventory = ((BWInventory) getComponent().getInventory()).mcInventory;
		slot = new net.minecraft.inventory.Slot(inventory, getComponent().getSlotID(), 0, 0);
		container.inventorySlots.add(slot);
	}
}
