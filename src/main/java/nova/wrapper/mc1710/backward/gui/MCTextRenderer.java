package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.gui.FontRenderer;
import nova.core.gui.render.text.FormattedText;
import nova.core.gui.render.text.FormattedText.TextFormat;
import nova.core.gui.render.text.TextRenderer;
import nova.core.render.Color;
import nova.core.util.math.MathUtil;
import nova.core.util.transform.Vector2d;
import nova.core.util.transform.Vector2i;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class MCTextRenderer implements TextRenderer {

	private final FontRenderer fontrenderer;
	private MCCanvas canvas;
	private int zIndex;

	public MCTextRenderer(FontRenderer fontrenderer, MCCanvas canvas) {
		this.fontrenderer = fontrenderer;
		this.canvas = canvas;
	}

	public void setCanvas(MCCanvas canvas) {
		this.canvas = canvas;
	}

	private static Pattern wordPattern = Pattern.compile("(%n|\n)|([^ ]+[ \n%n])| +|(.+)");
	private static Pattern pattern2 = Pattern.compile("^(?<! )[^ \n]+");

	private Paragraph<Word> unwrapByWord(FormattedText text, int width) {

		Paragraph<Word> unwrapped = new Paragraph<>(true);
		List<FormattedText> list = Lists.newArrayList(text);
		Line<Word> currentLine = new Line<>();
		unwrapped.lines.add(currentLine);

		float xOffset = 0;

		for (int i = 0; i < list.size(); i++) {

			FormattedText current = list.get(i);
			TextFormat format = current.getFormat();
			String string = current.getText();
			Matcher matcher = wordPattern.matcher(string);

			while (matcher.find()) {
				if (matcher.group(1) != null) {
					xOffset = 0;
					currentLine = new Line<>();
					unwrapped.lines.add(currentLine);
				} else {
					boolean flag = false;
					String string2 = matcher.group(2);
					if (string2 == null) {
						flag = true;
						string2 = matcher.group(3);
						if (string2 == null)
							continue;
					}

					Word word = new Word().append(new Text(string2, format));

					if (flag) {
						FormattedText next;
						int j = i;
						while (++j < list.size()) {
							next = list.get(j);
							String string3 = next.getText();
							Matcher matcher2 = pattern2.matcher(string3);

							if (matcher2.matches()) {
								i++;
								word.append(new Text(next.getText(), next.getFormat()));
								continue;
							} else {
								matcher2.reset();

								if (matcher2.find()) {
									word.append(new Text(matcher2.group(), next.getFormat()));
									list.set(j, new FormattedText(string3.substring(matcher2.end(), string3.length()), next.getFormat()));
								} else {
									break;
								}
							}
						}
						word.text.get(word.text.size() - 1).text += " ";
					}

					double wordWidth = word.getDimensions().x;
					if (xOffset + wordWidth > width) {
						xOffset = 0;
						currentLine = new Line<>();
						unwrapped.lines.add(currentLine);
					}
					xOffset += wordWidth;
					currentLine.append(word);
				}
			}
		}
		return unwrapped;
	}

	private Paragraph<Text> unwrapByFormat(FormattedText text) {
		Paragraph<Text> unwrapped = new Paragraph<>(false);
		Line<Text> currentLine = new Line<>();
		unwrapped.lines.add(currentLine);

		Text converted = null;
		TextFormat format = new TextFormat();

		for (FormattedText sub : text) {
			TextFormat nextFormat = sub.getFormat();
			String[] split = sub.getText().split("%n|\n", -1);

			for (int i = 0; i < split.length; i++) {
				if (i != 0 || (converted == null || nextFormat.shadow != format.shadow || nextFormat.size != format.size || nextFormat.color != format.color)) {
					if (i > 0) {
						currentLine = new Line<>();
						unwrapped.lines.add(currentLine);
					}
					converted = new Text(split[i], nextFormat);
					currentLine.append(converted);
				} else {
					converted.append(sub.getText(), nextFormat);
				}
			}
			format = nextFormat;
		}
		return unwrapped;
	}

	private String addFormat(String text, TextFormat format) {
		StringBuilder builder = new StringBuilder();
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
		return builder.toString();
	}

	private class Text implements RenderedText {

		private String text;
		private TextFormat format;

		private Text(String text, TextFormat format) {
			this.text = addFormat(text, format);
			this.format = format;
		}

		private Text append(String text, TextFormat format) {
			this.text += addFormat(text, format);
			return this;
		}

		@Override
		public Vector2d getDimensions() {
			float scale = format.size / (float) fontrenderer.FONT_HEIGHT;
			return new Vector2d(fontrenderer.getStringWidth(text) * scale, fontrenderer.FONT_HEIGHT * scale);
		}
	}

	private class Word implements RenderedText {

		private List<Text> text = new ArrayList<>();

		private Word append(Text text) {
			this.text.add(text);
			return this;
		}

		@Override
		public Vector2d getDimensions() {
			double width = 0, height = 0;
			for (Text text : text) {
				Vector2d dim = text.getDimensions();
				height = Math.max(height, dim.y);
				width += dim.x;
			}
			return new Vector2d(width, height);
		}
	}

	private static class Line<T extends RenderedText> implements RenderedText {

		private List<T> text = new ArrayList<>();

		private Line<T> append(T text) {
			this.text.add(text);
			return this;
		}

		@Override
		public Vector2d getDimensions() {
			double width = 0, height = 0;
			for (RenderedText text : text) {
				Vector2d dim = text.getDimensions();
				height = Math.max(height, dim.y);
				width += dim.x;
			}
			return new Vector2d(width, height);
		}
	}

	private static class Paragraph<T extends RenderedText> implements RenderedText {

		private List<Line<T>> lines = new ArrayList<>();
		private Vector2d dimension;
		private boolean wordWrapped;

		private Paragraph(boolean wordWrapped) {
			this.wordWrapped = wordWrapped;
		}

		@Override
		public Vector2d getDimensions() {
			if (dimension == null) {
				double width = 0, height = 0;
				for (RenderedText text : lines) {
					Vector2d dim = text.getDimensions();
					width = Math.max(height, dim.x);
					height += dim.y;
				}
				return dimension = new Vector2d(width, height);
			}
			return dimension;
		}
	}

	private int drawText(List<Text> text, int x, int y, int xOffset, int yOffset) {
		for (Text sub : text) {
			drawText(sub, x, y, xOffset, yOffset);
			xOffset += sub.getDimensions().x + 1;
		}
		return xOffset;
	}

	private void drawText(Text text, int x, int y, int xOffset, int yOffset) {
		float scale = text.format.size / (float) fontrenderer.FONT_HEIGHT;
		if (text.format.size != fontrenderer.FONT_HEIGHT) {
			GL11.glPushMatrix();
			GL11.glTranslatef(x + xOffset, y - fontrenderer.FONT_HEIGHT * scale + scale * 2F + yOffset, 0);
			GL11.glScalef(scale, scale, 0);
			GL11.glTranslatef(-x - xOffset, -y, 0);
			fontrenderer.drawString(text.text, x + xOffset, y, text.format.color.argb(), text.format.shadow);
			GL11.glPopMatrix();
		} else {
			fontrenderer.drawString(text.text, x + xOffset, y - fontrenderer.FONT_HEIGHT + 2 + yOffset, text.format.color.argb(), text.format.shadow);
		}
	}

	private <T extends RenderedText> Paragraph<T> getCached(FormattedText str, boolean wordWrapped, int width) {
		Paragraph<T> text;
		Optional<RenderedText> cached = str.getCached();
		if (cached.isPresent() && !((Paragraph<?>) cached.get()).wordWrapped) {
			text = (Paragraph<T>) cached.get();
		} else {
			text = (Paragraph<T>) (wordWrapped ? unwrapByWord(str, width) : unwrapByFormat(str));
			str.setCached(text);
		}
		return text;
	}

	@Override
	public void drawString(int x, int y, FormattedText str) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);

		Paragraph<Text> text = getCached(str, false, 0);

		int xOffset = 0;
		int yOffset = 0;

		for (int i = 0; i < text.lines.size(); i++) {
			Line<Text> line = text.lines.get(i);
			if (i == 0) {
				yOffset += line.getDimensions().y;
			}
			xOffset = drawText(line.text, x, y, xOffset, yOffset);
			if (i + 1 < text.lines.size()) {
				yOffset += text.lines.get(i + 1).getDimensions().y + 1;
				xOffset = 0;
			}
		}

		GL11.glTranslatef(0, 0, -zIndex);
	}

	@Override
	public void drawString(int x, int y, FormattedText str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		GL11.glTranslatef(0, 0, zIndex);
		Paragraph<Word> text = getCached(str, true, width);

		int xOffset = 0;
		int yOffset = 0;

		for (int i = 0; i < text.lines.size(); i++) {
			Line<Word> line = text.lines.get(i);
			if (i == 0) {
				yOffset += line.getDimensions().y;
			}
			for (Word word : line.text) {
				xOffset = drawText(word.text, x, y, xOffset, yOffset);
			}

			if (i + 1 < text.lines.size()) {
				yOffset += text.lines.get(i + 1).getDimensions().y + 1;
				xOffset = 0;
			}
		}

		GL11.glTranslatef(0, 0, -zIndex);
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
	public void drawCutString(int x, int y, FormattedText str, int width) {
		x += canvas.tx();
		y += canvas.ty();

		// TODO implement
		throw new UnsupportedOperationException();
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
		Vector2d dim = getCached(text, false, 0).getDimensions();
		return new Vector2i(dim.xi(), dim.yi());
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
