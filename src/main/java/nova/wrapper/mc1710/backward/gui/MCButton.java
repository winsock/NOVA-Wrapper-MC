package nova.wrapper.mc1710.backward.gui;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import nova.core.gui.GuiEvent.MouseEvent;
import nova.core.gui.GuiEvent.MouseEvent.EnumMouseState;
import nova.core.gui.Outline;
import nova.core.gui.components.Button;
import nova.core.gui.nativeimpl.NativeButton;
import nova.core.gui.render.Graphics;
import nova.core.util.transform.Vector2i;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCButton extends MCGuiComponent<Button> implements NativeButton, DrawableGuiComponent {

	@SideOnly(Side.CLIENT)
	private MCGuiButton button;

	public MCButton(Button component) {
		super(component);

		if (FMLCommonHandler.instance().getSide().isClient()) {
			button = new MCGuiButton();
		}

		component.onGuiEvent(this::onMousePressed, MouseEvent.class);
	}

	@Override
	public void setOutline(Outline outline) {
		button.width = outline.getWidth();
		button.height = outline.getHeight();
		super.setOutline(outline);
	}

	@Override
	public String getText() {
		return button.displayString;
	}

	@Override
	public Optional<Vector2i> getMinimumSize() {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		return fontRenderer != null ? Optional.of(new Vector2i(fontRenderer.getStringWidth(button.displayString) + 10, fontRenderer.FONT_HEIGHT + 10)) : Optional.empty();
	}

	@Override
	public void setText(String text) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			button.displayString = text;
		}
	}

	@Override
	public boolean isPressed() {
		// TODO
		return false;
	}

	@Override
	public void setPressed(boolean isPressed) {
		// TODO
	}

	@Override
	public void draw(int mouseX, int mouseY, float partial, Graphics graphics) {
		button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
		getComponent().render(mouseX, mouseY, graphics);
	}

	public void onMousePressed(MouseEvent event) {
		if (event.state == EnumMouseState.DOWN) {
			if (button.mousePressed(Minecraft.getMinecraft(), event.mouseX, event.mouseY)) {
				button.func_146113_a(Minecraft.getMinecraft().getSoundHandler());
			}
		} else if (event.state == EnumMouseState.UP) {
			button.mouseReleased(event.mouseX, event.mouseY);
		}
	}

	@SideOnly(Side.CLIENT)
	public class MCGuiButton extends GuiButtonExt {

		public MCGuiButton() {
			super(0, 0, 0, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			MCCanvas canvas = getCanvas();
			xPosition = (int) canvas.tx();
			yPosition = (int) canvas.ty();
			super.drawButton(mc, mouseX, mouseY);
		}
	}
}
