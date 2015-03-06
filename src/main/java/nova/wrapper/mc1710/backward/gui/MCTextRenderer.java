package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.gui.FontRenderer;
import nova.core.gui.render.FormattedText;
import nova.core.gui.render.FormattedText.TextFormat;
import nova.core.gui.render.TextRenderer;
import nova.core.render.Color;
import nova.core.util.math.MathUtil;
import nova.core.util.transform.Vector2i;

import org.lwjgl.opengl.GL11;

public class MCTextRenderer implements TextRenderer {

	private final FontRenderer fontrenderer;
	private MCCanvas canvas;
	private int zIndex;

	private static final Pattern pattern = Pattern.compile("(\u00A7s)|(\u00A7c([-+]?\\d+)\u00A7)");

	public MCTextRenderer(FontRenderer fontrenderer, MCCanvas canvas) {
		this.fontrenderer = fontrenderer;
		this.canvas = canvas;
	}

	public void setCanvas(MCCanvas canvas) {
		this.canvas = canvas;
	}

	private static String unwrap(FormattedText text) {
		TextFormat format = new TextFormat();
		StringBuilder builder = new StringBuilder();
		for (FormattedText sub : text) {
			unwrap(builder, sub, format);
			format = sub.getFormat();
		}
		return builder.toString();
	}

	private static void unwrap(StringBuilder builder, FormattedText text, TextFormat prev) {
		TextFormat format = text.getFormat();

		if (prev.shadow != format.shadow)
			builder.append("\u00A7s");
		if (prev.color != format.color)
			builder.append("\u00A7c").append(format.color.argb()).append("\u00A7");

		addFormat(builder, text.getText(), format);
	}

	private static void addFormat(StringBuilder builder, String text, TextFormat format) {
		if (format.bold)
			builder.append("\u00A7l");
		if (format.italic)
			builder.append("\u00A7o");
		if (format.strikethrough)
			builder.append("\u00A7m");
		if (format.underline)
			builder.append("\u00A7n");

		builder.append(text);
		builder.append("\u00A7r");
	}

	private static String addFormat(String text, TextFormat format) {
		StringBuilder builder = new StringBuilder();
		addFormat(builder, text, format);
		return builder.toString();
	}

	private static List<Text> split(String text) {
		List<Text> unwrapped = new ArrayList<>();
		Matcher matcher = pattern.matcher(text);

		boolean shadow = false;
		Color color = Color.black;

		int index = 0;
		while (matcher.find()) {
			boolean newShadow = shadow;
			Color newColor = color;

			if (matcher.group(1) != null) {
				newShadow = !newShadow;
			} else {
				newColor = Color.argb(Integer.parseInt(matcher.group(3)));
			}

			if (newShadow != shadow || newColor.argb() != color.argb()) {
				String sub = text.substring(index, matcher.start());
				if (sub.length() > 0)
					unwrapped.add(new Text(sub, color, shadow));
				index = matcher.end();
			}

			shadow = newShadow;
			color = newColor;
		}

		if (index < text.length()) {
			String trail = text.substring(index, text.length());
			unwrapped.add(new Text(trail, color, shadow));
		}

		return unwrapped;
	}

	private static class Text {

		private String text;
		private final Color color;
		private final boolean shadow;

		private Text(String text, Color color, boolean shadow) {
			this.text = pattern.matcher(text).replaceAll("");
			this.color = color;
			this.shadow = shadow;
		}
	}

	@Override
	public void drawString(int x, int y, FormattedText str) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);
		List<Text> text = split(unwrap(str));
		int xOffset = 0;
		for (Text sub : text) {
			fontrenderer.drawString(sub.text, x + xOffset, y, sub.color.argb(), sub.shadow);
			xOffset += fontrenderer.getStringWidth(sub.text) + 1;
		}
		GL11.glTranslatef(0, 0, -zIndex);
	}

	@Override
	public void drawString(int x, int y, FormattedText str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);

		int xOffset = 0;
		int yOffset = 0;
		int spaceWidth = fontrenderer.getCharWidth(' ');

		TextFormat format = new TextFormat();
		for (FormattedText sub : str) {
			format = sub.getFormat();
			String[] words = sub.getText().split(" +");
			for (String word : words) {
				word = addFormat(word, format);
				int wordWidth = fontrenderer.getStringWidth(word);

				if (xOffset + wordWidth > width) {
					xOffset = 0;
					yOffset += fontrenderer.FONT_HEIGHT + 1;
				}

				fontrenderer.drawString(word, x + xOffset, y + yOffset, format.color.argb(), format.shadow);
				xOffset += wordWidth > 0 ? wordWidth + spaceWidth : 0;
			}
		}

		GL11.glTranslatef(0, 0, -zIndex);
	}

	@Override
	public void drawCutString(int x, int y, FormattedText str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		// TODO implement
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(int x, int y, String str) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);
		fontrenderer.drawString(str.replaceAll("\u00A7", ""), x, y, Color.black.argb());
		GL11.glTranslatef(0, 0, -zIndex);
	}

	@Override
	public void drawString(int x, int y, String str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);
		fontrenderer.drawSplitString(str.replaceAll("\u00A7", "").replaceAll("%n", "\n"), x, y, width, Color.black.argb());
		GL11.glTranslatef(0, 0, -zIndex);
	}

	@Override
	public void drawCutString(int x, int y, String str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		// TODO implement
		throw new UnsupportedOperationException();
	}

	@Override
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public Vector2i getBounds(FormattedText text) {
		return getBounds(text.getText());
	}

	@Override
	public Vector2i getBounds(String str) {
		int height = 0, width = 0;
		for (String line : str.split("%n|\n")) {
			width = MathUtil.max(width, fontrenderer.getStringWidth(line));
			height += fontrenderer.FONT_HEIGHT;
		}
		return new Vector2i(width, height);
	}
}
