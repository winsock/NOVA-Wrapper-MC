package nova.wrapper.mc1710.backward.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import nova.core.game.Game;
import nova.core.gui.Gui;
import nova.core.gui.GuiComponent;
import nova.core.gui.GuiEvent.MouseEvent.EnumMouseButton;
import nova.core.gui.Outline;
import nova.core.gui.nativeimpl.NativeGui;
import nova.core.gui.render.Graphics;
import nova.core.gui.render.text.TextMetrics;
import nova.core.network.Packet;
import nova.wrapper.mc1710.network.discriminator.PacketGui;
import nova.wrapper.mc1710.network.netty.MCNetworkManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCGui extends MCGuiContainer implements NativeGui, DrawableGuiComponent {

	private final Gui component;

	private Outline outline = Outline.empty;
	private Graphics graphics;
	private MCTextRenderer textRenderer;

	@SideOnly(Side.CLIENT)
	private MCGuiScreen guiScreen;
	private MCContainer container;

	public MCGui(Gui component) {
		super(component);
		this.component = component;

		if (FMLCommonHandler.instance().getSide().isClient()) {
			guiScreen = new MCGuiScreen();
		}
	}

	@SideOnly(Side.CLIENT)
	public MCGuiScreen getGuiScreen() {
		return guiScreen;
	}

	public MCContainer newContainer() {
		container = new MCContainer();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			guiScreen.inventorySlots = container;
		}
		for (GuiComponent<?, ?> component : components) {
			((DrawableGuiComponent) component.getNative()).onAddedToContainer(container);
		}
		return container;
	}

	@Override
	public MCCanvas getCanvas() {
		return (MCCanvas) graphics.getCanvas();
	}

	@Override
	public TextMetrics getTextMetrics() {
		return textRenderer;
	}

	@Override
	public Gui getComponent() {
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

		for (GuiComponent<?, ?> component : components) {
			Outline outline = component.getOutline();
			graphics.getCanvas().translate(outline.x1i(), outline.y1i());
			((DrawableGuiComponent) component.getNative()).draw(mouseX, mouseY, partial, graphics);
			graphics.getCanvas().translate(-outline.x1i(), -outline.y1i());
		}

		Outline outline = getOutline();
		graphics.getCanvas().translate(outline.x1i(), outline.y1i());
		getComponent().render(mouseX, mouseY, graphics);
		graphics.getCanvas().translate(-outline.x1i(), -outline.y1i());
	}

	public class MCContainer extends Container {

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return true;
		}

		public MCGui getGui() {
			return MCGui.this;
		}

		@Override
		public void onContainerClosed(EntityPlayer player) {
			super.onContainerClosed(player);
			getGui().component.unbind();
		}
	}

	private static final Container fakeContainer = new Container() {
		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return false;
		}
	};

	@SideOnly(Side.CLIENT)
	public class MCGuiScreen extends GuiContainer {

		public MCGuiScreen() {
			super(MCGui.this.container);
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partial) {
			Container container = inventorySlots;
			// Replace container instance with fake container in order to stop
			// slot rendering
			inventorySlots = fakeContainer;
			super.drawScreen(mouseX, mouseY, partial);
			// Back to where it belongs
			inventorySlots = container;
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			MCGui.this.draw(mouseX, mouseY, partial, graphics);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
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

			MCCanvas canvas = new MCCanvas(width, height, Tessellator.instance);
			if (textRenderer == null)
				textRenderer = new MCTextRenderer(fontRendererObj, canvas);

			textRenderer.setCanvas(canvas);
			graphics = new Graphics(canvas, textRenderer);

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
