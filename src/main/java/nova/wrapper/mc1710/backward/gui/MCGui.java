package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import nova.core.entity.Entity;
import nova.core.game.Game;
import nova.core.gui.Gui;
import nova.core.gui.GuiComponent;
import nova.core.gui.GuiEvent.MouseEvent.EnumMouseButton;
import nova.core.gui.Outline;
import nova.core.gui.nativeimpl.NativeGui;
import nova.core.gui.render.Graphics;
import nova.core.gui.render.TextRenderer;
import nova.core.network.Packet;
import nova.core.util.transform.Vector3i;
import nova.wrapper.mc1710.network.discriminator.PacketGui;
import nova.wrapper.mc1710.network.netty.MCNetworkManager;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCGui implements NativeGui, DrawableGuiComponent {

	private Gui component;
	private List<GuiComponent<?, ?>> components = new ArrayList<>();
	private Outline outline = Outline.empty;
	private Graphics graphics;
	private TextRenderer textRenderer;
	
	@SideOnly(Side.CLIENT)
	private MCGuiScreen guiScreen;
	private MCContainer container;

	public MCGui(Gui component) {
		this.component = component;

		if (FMLCommonHandler.instance().getSide().isClient()) {
			guiScreen = new MCGuiScreen();
		}
	}

	@SideOnly(Side.CLIENT)
	public MCGuiScreen getGuiScreen() {
		return guiScreen;
	}

	public MCContainer getContainer() {
		return container;
	}

	@Override
	public void bind(Entity entity, Vector3i pos) {
		// Create a new container instance because containers aren't state safe.
		container = new MCContainer();
	}

	@Override
	public Gui getComponent() {
		return component;
	}
	
	@Override
	public void addElement(GuiComponent<?, ?> component) {
		components.add(component);
	}

	@Override
	public void removeElement(GuiComponent<?, ?> component) {
		components.remove(component);
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
		// Not needed as it gets redrawn every frame
	}

	@Override
	public void dispatchNetworkEvent(Packet packet) {
		// TODO I think the NetworkManager should be able to do this
		MCNetworkManager manager = (MCNetworkManager) Game.instance.networkManager;
		manager.sendToServer(new PacketGui(packet));
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		guiScreen.drawScreen(mouseX, mouseY, partial);
	}

	public class MCContainer extends Container {

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return true;
		}

		public MCGui getGui() {
			return MCGui.this;
		}
	}

	@SideOnly(Side.CLIENT)
	public class MCGuiScreen extends GuiScreen {

		@Override
		public void drawScreen(int mouseX, int mouseY, float partial) {
			components.forEach((component) -> ((DrawableGuiComponent) component.getNative()).draw(mouseX, mouseY, partial, graphics));
			Outline outline = getOutline();
			graphics.getCanvas().translate(outline.x1i(), outline.y1i());
			getComponent().render(mouseX, mouseY, graphics);
			graphics.getCanvas().translate(-outline.x1i(), -outline.y1i());
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int button) {
			onMousePressed(mouseX, mouseY, getMouseButton(button), true);
		}

		@Override
		protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
			onMousePressed(mouseX, mouseY, getMouseButton(button), false);
		}

		private EnumMouseButton getMouseButton(int button) {
			switch (button) {
				case 0:
				default:
					return EnumMouseButton.LEFT;
				case 1:
					return EnumMouseButton.RIGHT;
				case 2:
					return EnumMouseButton.MIDDLE;
			}
		}

		@Override
		public void handleKeyboardInput() {
			boolean state = Keyboard.getEventKeyState();
			int key = Keyboard.getEventKey();
			char ch = Keyboard.getEventCharacter();
			onKeyPressed(Game.instance.keyManager.getKey(key), ch, state);
			if (state)
				keyTyped(ch, key);

			this.mc.func_152348_aa();
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}

		@Override
		public void setWorldAndResolution(Minecraft mc, int width, int height) {
			super.setWorldAndResolution(mc, width, height);

			if (textRenderer == null)
				textRenderer = new MCTextRenderer(fontRendererObj);

			graphics = new Graphics(new MCCanvas(width, height, Tessellator.instance), textRenderer);

			boolean resized = width != outline.getWidth() || height != outline.getHeight();
			Outline oldOutline = outline;
			outline = new Outline(0, 0, width, height);

			if (resized)
				onResized(oldOutline);
		}

		public MCGui getGui() {
			return MCGui.this;
		}
	}
}
