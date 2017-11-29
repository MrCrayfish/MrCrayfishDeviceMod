package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.interfaces.IHighlight;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TextArea extends Component
{
	private static final String UNFORMATTED_SPLIT = "(?<=%1$s)|(?=%1$s)";
	private static final String[] DELIMITERS = {"(\\s|$)(?=(([^\"]*\"){2})*[^\"]*$)", "[\\p{Punct}&&[^@\"]]", "\\p{Digit}+"};
	private static final String SPLIT_REGEX;
	static
	{
		StringJoiner joiner = new StringJoiner("|");
		for(String s : DELIMITERS)
		{
			joiner.add(s);
		}
		SPLIT_REGEX = String.format(UNFORMATTED_SPLIT, "(" + joiner.toString() + ")");
	}

	protected FontRenderer fontRendererObj;
	protected int width, height;
	protected int padding = 4;
	protected String placeholder = null;
	protected boolean isFocused = false;
	protected boolean editable = true;

	private List<String> lines = new ArrayList<>();
	private int visibleLines;
	private ScrollBar scrollBar;
	private int scrollBarSize = 3;
	private int horizontalScroll;
	private int verticalScroll;
	private int horizontalOffset;
	private int verticalOffset;
	private int cursorTick = 0;
	private int cursorX;
	private int cursorY;
	private int clickedX;
	private int clickedY;
	private boolean wrapText = false;
	private int maxLineWidth;
	private IHighlight highlight = null;

	/* Personalisation */
	protected int placeholderColour = new Color(1.0F, 1.0F, 1.0F, 0.35F).getRGB();
	protected int textColour = Color.WHITE.getRGB();
	protected int backgroundColour = Color.DARK_GRAY.getRGB();
	protected int borderColour = Color.BLACK.getRGB();

	/**
	 * Default text area constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the width of the text area
	 * @param height the height of the text area
	 */
	public TextArea(int left, int top, int width, int height)
	{
		super(left, top);
		this.fontRendererObj = Laptop.fontRenderer;
		this.width = width;
		this.height = height;
		this.visibleLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
		this.lines.add("");
	}

	@Override
	public void handleTick()
	{
		cursorTick++;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if (this.visible)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
			Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);

			if(!isFocused && placeholder != null && (lines.isEmpty() || (lines.size() == 1 && lines.get(0).isEmpty())))
			{
				GlStateManager.enableBlend();
				mc.fontRendererObj.drawSplitString(placeholder, x + padding + 1, y + padding + 2, width - padding * 2 - 2, placeholderColour);
			}

			GLHelper.scissor(x + padding, y + padding, width - padding * 2, height - padding * 2);

			for(int i = 0; i < visibleLines && i + verticalScroll < lines.size(); i++)
			{
				float scrollPercentage = (verticalScroll + verticalOffset) / (float) (lines.size() - visibleLines);
				float pixelsPerUnit = (float) maxLineWidth / (float) (width - padding * 2);
				int scrollX = MathHelper.clamp(horizontalScroll + (int)(horizontalOffset * pixelsPerUnit), 0, Math.max(0, maxLineWidth - (width - padding * 2)));
				int scrollY = (int) ((lines.size() - visibleLines) * (scrollPercentage));
				int lineY = i + MathHelper.clamp(scrollY, 0, Math.max(0, lines.size() - visibleLines));
				if(highlight != null)
				{
					String[] words = lines.get(lineY).split(SPLIT_REGEX);
					StringBuilder builder = new StringBuilder();
					for(String word : words)
					{
						TextFormatting[] formatting = highlight.getKeywordFormatting(word);
						for(TextFormatting format : formatting)
						{
							builder.append(format);
						}
						builder.append(word);
						builder.append(TextFormatting.RESET);
					}
					fontRendererObj.drawString(builder.toString(), x + padding - scrollX, y + padding + i * fontRendererObj.FONT_HEIGHT, -1);
				}
				else
				{
					fontRendererObj.drawString(lines.get(lineY), x + padding - scrollX, y + padding + i * fontRendererObj.FONT_HEIGHT, textColour);
				}
			}

			GL11.glDisable(GL11.GL_SCISSOR_TEST);

			float linesPerUnit = (float) lines.size() / (float) visibleLines;
			int scroll = MathHelper.clamp(verticalScroll + verticalOffset * (int) linesPerUnit, 0, Math.max(0, lines.size() - visibleLines));
			if(this.isFocused && cursorY >= scroll && cursorY < scroll + visibleLines)
			{
				if ((this.cursorTick / 10) % 2 == 0)
				{
					String subString = getActiveLine().substring(0, cursorX);
					int visibleWidth = width - padding * 2;
					float pixelsPerUnit = (float) maxLineWidth / (float) (width - padding * 2);
					int posX = x + padding + fontRendererObj.getStringWidth(subString) - MathHelper.clamp(horizontalScroll - (int) (horizontalOffset * pixelsPerUnit), 0, Math.max(0, maxLineWidth - visibleWidth));
					int posY = y + padding + (cursorY - scroll) * fontRendererObj.FONT_HEIGHT;
					Gui.drawRect(posX, posY - 1, posX + 1, posY + fontRendererObj.FONT_HEIGHT - 1, Color.WHITE.getRGB());
				}
			}

			if(lines.size() > visibleLines)
			{
				int visibleScrollBarHeight = height - 4;
				int scrollBarHeight = Math.max(20, (int) ((float) visibleLines / (float) lines.size() * (float) visibleScrollBarHeight));
				float scrollPercentage = MathHelper.clamp((verticalScroll + verticalOffset) / (float) (lines.size() - visibleLines), 0.0F, 1.0F);
				int scrollBarY = (int) ((visibleScrollBarHeight - scrollBarHeight) * scrollPercentage);
				int scrollY = yPosition + 2 + scrollBarY;
				Gui.drawRect(x + width - 2 - scrollBarSize, scrollY, x + width - 2, scrollY + scrollBarHeight, placeholderColour);
			}

			if(!wrapText && maxLineWidth >= width - padding * 2)
			{
				int visibleWidth = width - padding * 2;
				int visibleScrollBarWidth = width - 4 - (lines.size() > visibleLines ? scrollBarSize + 1 : 0);
				float scrollPercentage = (float) (horizontalScroll + 1) / (float) (maxLineWidth - visibleWidth + 1);
				int scrollBarWidth = Math.max(20, (int) ((float) visibleWidth / (float) maxLineWidth * (float) visibleScrollBarWidth));
				int relativeScrollX = (int) (scrollPercentage * (visibleScrollBarWidth - scrollBarWidth));
				int scrollX = xPosition + 2 + MathHelper.clamp(relativeScrollX + horizontalOffset, 0, visibleScrollBarWidth - scrollBarWidth);
				Gui.drawRect(scrollX, y + height - scrollBarSize - 2, scrollX + scrollBarWidth, y + height - 2, placeholderColour);
			}
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled || !this.editable)
			return;

		this.isFocused = GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height);

		ScrollBar scrollBar = isMouseInsideScrollBar(mouseX, mouseY);
		if(scrollBar != null)
		{
			this.scrollBar = scrollBar;
			switch(scrollBar)
			{
				case HORIZONTAL:
					clickedX = mouseX;
					break;
				case VERTICAL:
					clickedY = mouseY;
					break;
			}
			return;
		}

		if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition + padding, yPosition + padding, width - padding * 2, height - padding * 2))
		{
			int lineX = mouseX - xPosition - padding + horizontalScroll;
			int lineY = Math.min((mouseY - yPosition - padding) / fontRendererObj.FONT_HEIGHT + verticalScroll, Math.max(0, lines.size() - 1));
			cursorX = getClosestLineIndex(lineX, lineY);
			cursorY = lineY;
			cursorTick = 0;
		}
	}

	@Override
	protected void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		if(scrollBar != null)
		{
			switch(scrollBar)
			{
				case HORIZONTAL:
					horizontalOffset = mouseX - clickedX;
					break;
				case VERTICAL:
					int visibleScrollBarHeight = height - 4;
					int scrollBarHeight = Math.max(20, (int) ((float) visibleLines / (float) lines.size() * (float) visibleScrollBarHeight));
					float spacing = (float) (visibleScrollBarHeight - scrollBarHeight) / (float) (lines.size() - visibleLines);
					verticalOffset = (int) ((mouseY - clickedY) / spacing);
					break;
			}
		}
	}

	@Override
	protected void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		if(scrollBar != null)
		{
			switch(scrollBar)
			{
				case HORIZONTAL:
				{
					float scrollPercentage = (float) maxLineWidth / (float) (width - padding * 2);
					horizontalScroll = MathHelper.clamp(horizontalScroll + (int)(horizontalOffset * scrollPercentage), 0, maxLineWidth - (width - padding * 2));
					break;
				}
				case VERTICAL:
				{
					float scrollPercentage = MathHelper.clamp((verticalScroll + verticalOffset) / (float) (lines.size() - visibleLines), 0.0F, 1.0F);
					verticalScroll = (int) ((lines.size() - visibleLines) * (scrollPercentage));
					break;
				}
			}
			horizontalOffset = 0;
			verticalOffset = 0;
			scrollBar = null;
		}
	}

	@Override
	public void handleKeyTyped(char character, int code)
	{
		if(!this.visible || !this.enabled || !this.isFocused || !this.editable)
			return;

		if (GuiScreen.isKeyComboCtrlV(code))
		{
			String[] lines = GuiScreen.getClipboardString().split("\n");
			for(int i = 0; i < lines.length - 1; i++)
			{
				insertAtCursor(lines[i] + "\n");
			}
			insertAtCursor(lines[lines.length - 1]);
		}
		else
		{
			switch (code)
			{
				case Keyboard.KEY_BACK:
					handleBackspace();
					return;
				case Keyboard.KEY_RETURN:
					handleReturn();
					return;
				case Keyboard.KEY_TAB:
					insertAtCursor('\t');
					return;
				case Keyboard.KEY_LEFT:
					moveCursorLeft(1);
					return;
				case Keyboard.KEY_RIGHT:
					moveCursorRight(1);
					return;
				case Keyboard.KEY_UP:
					moveCursorUp();
					return;
				case Keyboard.KEY_DOWN:
					moveCursorDown();
					return;
				default:
					if (ChatAllowedCharacters.isAllowedCharacter(character))
					{
						insertAtCursor(character);
					}
			}
		}
	}

	@Override
	protected void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height))
		{
			scroll(direction ? -1 : 1);
		}
	}

	@Nullable
	private ScrollBar isMouseInsideScrollBar(int mouseX, int mouseY)
	{
		if(lines.size() > visibleLines)
		{
			int visibleScrollBarHeight = height - 4;
			float scrollPercentage = (float) verticalScroll / (float) (lines.size() - visibleLines);
			int scrollBarHeight = Math.max(20, (int) ((float) visibleLines / (float) lines.size() * (float) visibleScrollBarHeight));
			int relativeScrollY = (int) (scrollPercentage * (visibleScrollBarHeight - scrollBarHeight));
			int posX = xPosition + width - 2 - scrollBarSize;
			int posY = yPosition + 2 + MathHelper.clamp(relativeScrollY + verticalOffset, 0, visibleScrollBarHeight - scrollBarHeight);
			if(GuiHelper.isMouseInside(mouseX, mouseY, posX, posY, posX + scrollBarSize, posY + scrollBarHeight))
			{
				return ScrollBar.VERTICAL;
			}
		}

		if(!wrapText && maxLineWidth >= width - padding * 2)
		{
			int visibleWidth = width - padding * 2;
			int visibleScrollBarWidth = width - 4 - (lines.size() > visibleLines ? scrollBarSize + 1 : 0);
			float scrollPercentage = (float) horizontalScroll / (float) (maxLineWidth - visibleWidth + 1);
			int scrollBarWidth = Math.max(20, (int) ((float) visibleWidth / (float) maxLineWidth * (float) visibleScrollBarWidth));
			int relativeScrollX = (int) (scrollPercentage * (visibleScrollBarWidth - scrollBarWidth));
			int posX = xPosition + 2 + MathHelper.clamp(relativeScrollX, 0, visibleScrollBarWidth - scrollBarWidth);
			int posY = yPosition + height - 2 - scrollBarSize;
			if(GuiHelper.isMouseInside(mouseX, mouseY, posX, posY, posX + scrollBarWidth, posY + scrollBarSize))
			{
				return ScrollBar.HORIZONTAL;
			}
		}
		return null;
	}

	private String getActiveLine()
	{
		return lines.get(cursorY);
	}

	private void handleBackspace()
	{
		if(cursorY == 0 && cursorX == 0)
			return;

		removeCharAtCursor();
		if(wrapText)
		{
			if(cursorY + 1 < lines.size())
			{
				String activeLine = getActiveLine();
				if(activeLine.contains("\n"))
					return;

				String result = activeLine + lines.remove(cursorY + 1);
				if(fontRendererObj.getStringWidth(result) > width - padding * 2)
				{
					String trimmed = fontRendererObj.trimStringToWidth(result, width - padding * 2);
					lines.set(cursorY, trimmed);
					if(trimmed.charAt(trimmed.length() - 1) != '\n')
					{
						prependToLine(cursorY + 1, result.substring(trimmed.length()));
					}
					else if(cursorY + 1 < lines.size())
					{
						lines.add(cursorY + 1, trimmed);
					}
					else
					{
						lines.add(trimmed);
					}
				}
				else
				{
					lines.set(cursorY, result);
				}
			}
		}
		recalculateMaxWidth();
	}

	private void handleReturn()
	{
		int lineIndex = cursorY;
		String activeLine = getActiveLine();

		//if cursorX is equal to length, line doesn't have new line char
		if(cursorX == activeLine.length())
		{
			lines.set(lineIndex, activeLine + "\n");
			if(!wrapText || lineIndex + 1 == lines.size())
			{
				lines.add(lineIndex + 1, "");
			}
		}
		else
		{
			lines.set(lineIndex, activeLine.substring(0, cursorX) + "\n");
			lines.add(lineIndex + 1, activeLine.substring(cursorX));
		}

		if(cursorY + 1 >= verticalScroll + visibleLines)
		{
			scroll(1);
		}
		moveCursorRight(1);
		recalculateMaxWidth();
	}

	private void removeCharAtCursor()
	{
		String activeLine = getActiveLine();
		if(cursorX > 0)
		{
			String head = activeLine.substring(0, cursorX - 1);
			String tail = activeLine.substring(cursorX);
			lines.set(cursorY, head + tail);
			moveCursorLeft(1);
			return;
		}

		if(activeLine.isEmpty() || (activeLine.length() == 1 && activeLine.charAt(0) == '\n'))
		{
			if(verticalScroll > 0)
			{
				scroll(-1);
				moveYCursor(1);
			}
		}

		if(wrapText)
		{
			if(activeLine.isEmpty())
			{
				lines.remove(cursorY);
			}
			String previousLine = lines.get(cursorY - 1);
			lines.set(cursorY - 1, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)));
			moveCursorLeft(1);
		}
		else
		{
			String previousLine = lines.get(cursorY - 1);
			moveCursorLeft(1);
			if(!activeLine.isEmpty())
			{
				lines.set(cursorY, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)) + activeLine);
			}
			else
			{
				lines.set(cursorY, previousLine.substring(0, Math.max(previousLine.length() - 1, 0)));
			}
			lines.remove(cursorY + 1);
			if(verticalScroll + visibleLines == lines.size() - 1)
			{
				scroll(-1);
			}
		}
		recalculateMaxWidth();
	}

	private void insertAtCursor(char c)
	{
		int prevCursorY = cursorY;
		insertAtCursor(Character.toString(c));
		if(wrapText && prevCursorY != cursorY)
		{
			moveCursorRight(1);
		}
	}

	private void insertAtCursor(String text)
	{
		text = text.replace("\r", "");
		String activeLine = getActiveLine();
		String head = activeLine.substring(0, cursorX);
		String tail = activeLine.substring(cursorX);
		if(wrapText)
		{
			if(text.endsWith("\n"))
			{
				String result = head + text;
				if(fontRendererObj.getStringWidth(result) > width - padding * 2)
				{
					String trimmed = fontRendererObj.trimStringToWidth(result, width - padding * 2);
					lines.set(cursorY, trimmed);
					prependToLine(cursorY + 1, result.substring(trimmed.length()));
				}
				else
				{
					lines.set(cursorY, result);
				}
				prependToLine(cursorY + 1, tail);
			}
			else
			{
				String result = head + text + tail;
				if(fontRendererObj.getStringWidth(result) > width - padding * 2)
				{
					String trimmed = fontRendererObj.trimStringToWidth(result, width - padding * 2);
					lines.set(cursorY, trimmed);
					prependToLine(cursorY + 1, result.substring(trimmed.length()));
				}
				else
				{
					lines.set(cursorY, result);
				}
			}
		}
		else
		{
			if(text.endsWith("\n"))
			{
				lines.set(cursorY, head + text);
				prependToLine(cursorY + 1, tail);
			}
			else
			{
				lines.set(cursorY, head + text + tail);
			}
		}
		moveCursorRight(text.length());
		recalculateMaxWidth();
	}

	private void prependToLine(int lineIndex, String text)
	{
		if(lineIndex == lines.size())
			lines.add("");

		if(text.length() <= 0)
			return;

		if(lineIndex < lines.size())
		{
			if(text.charAt(Math.max(0, text.length() - 1)) == '\n')
			{
				lines.add(lineIndex, text);
				return;
			}
			String result = text + lines.get(lineIndex);
			if(fontRendererObj.getStringWidth(result) > width - padding * 2)
			{
				String trimmed = fontRendererObj.trimStringToWidth(result, width - padding * 2);
				lines.set(lineIndex, trimmed);
				prependToLine(lineIndex + 1, result.substring(trimmed.length()));
			}
			else
			{
				lines.set(lineIndex, result);
			}
		}
	}

	private void moveCursorRight(int amount)
	{
		if(amount <= 0)
			return;

		String activeLine = getActiveLine();

		if(cursorY == lines.size() - 1 && cursorX == activeLine.length() || (cursorX > 0 && activeLine.charAt(cursorX - 1) == '\n'))
			return;

		cursorTick = 0;

		if(cursorX < activeLine.length() && activeLine.charAt(cursorX) != '\n')
		{
			cursorX++;
		}
		else if(cursorY + 1 < lines.size())
		{
			cursorX = 0;
			if(cursorY >= verticalScroll + visibleLines - 1)
			{
				scroll(1);
			}
			moveYCursor(1);
		}

		if(!wrapText)
		{
			String line = lines.get(cursorY);
			int visibleWidth = width - padding * 2;
			int textWidth = fontRendererObj.getStringWidth(line.substring(0, cursorX));
			if(textWidth > visibleWidth || cursorX == line.length() || line.charAt(cursorX) == '\n')
			{
				horizontalScroll = Math.max(0, textWidth - visibleWidth + 1);
			}
			else if(cursorX == 0)
			{
				horizontalScroll = 0;
			}
		}

		moveCursorRight(amount - 1);
	}

	private void moveCursorLeft(int amount)
	{
		if(amount <= 0)
			return;

		if(cursorX == 0 && cursorY == 0)
			return;

		cursorTick = 0;
		if(cursorX > 0)
		{
			cursorX--;
		}
		else
		{
			cursorX = lines.get(cursorY - 1).length();

			if(cursorX > 0 && lines.get(cursorY - 1).charAt(cursorX - 1) == '\n')
			{
				cursorX--;
			}

			if(cursorY - 1 < verticalScroll)
			{
				scroll(-1);
			}
			moveYCursor(-1);
		}

		if(!wrapText)
		{
			String line = lines.get(cursorY);
			int visibleWidth = width - padding * 2;
			int textWidth = fontRendererObj.getStringWidth(lines.get(cursorY).substring(0, cursorX));
			if(textWidth < horizontalScroll)
			{
				horizontalScroll = Math.max(0, textWidth - 1);
			}
			else if(cursorX == line.length() || line.charAt(cursorX) == '\n')
			{
				horizontalScroll = Math.max(0, textWidth - visibleWidth + 1);
			}
			else if(cursorX == 0)
			{
				horizontalScroll = 0;
			}
		}

		moveCursorLeft(amount - 1);
	}

	private void moveCursorUp()
	{
		if(cursorY == 0)
			return;

		cursorTick = 0;
		String previousLine = lines.get(cursorY - 1);
		if(cursorX >= previousLine.length())
		{
			cursorX = previousLine.length();
			if(previousLine.contains("\n"))
			{
				cursorX--;
			}
		}
		if(cursorY - 1 < verticalScroll)
		{
			scroll(-1);
		}
		moveYCursor(-1);

		updateHorizontalScroll();
	}

	private void moveCursorDown()
	{
		if(cursorY == lines.size() - 1)
			return;

		cursorTick = 0;
		String nextLine = lines.get(cursorY + 1);
		if(cursorX > nextLine.length())
		{
			cursorX = nextLine.length();
			if(nextLine.endsWith("\n"))
			{
				cursorX--;
			}
		}
		if(cursorY + 1 >= verticalScroll + visibleLines)
		{
			scroll(1);
		}
		moveYCursor(1);

		updateHorizontalScroll();
	}

	private void moveYCursor(int amount)
	{
		cursorY += amount;
		if(cursorY < 0)
		{
			cursorY = 0;
			cursorX = 0;
		}
		if(cursorY >= lines.size())
		{
			cursorX = lines.get(lines.size() - 1).length();
			cursorY = lines.size() - 1;
		}
	}

	private void scroll(int amount)
	{
		verticalScroll += amount;
		if(verticalScroll < 0)
		{
			verticalScroll = 0;
		}
		else if(verticalScroll > lines.size() - visibleLines)
		{
			verticalScroll = Math.max(0, lines.size() - visibleLines);
		}
	}

    /**
     * Converts the text from wrapped lines to single lines and vice versa.
     */
	private void updateText()
	{
		List<String> updatedLines = new ArrayList<>();
		if(wrapText)
		{
			for(int i = 0; i < lines.size() - 1; i++)
			{
				String line = lines.get(i);
				if(line.equals("\n"))
				{
					updatedLines.add(line);
					continue;
				}

				List<String> split = fontRendererObj.listFormattedStringToWidth(lines.get(i), width - padding * 2);
				for(int j = 0; j < split.size() - 1; j++)
				{
					updatedLines.add(split.get(j));
				}
				if(split.size() > 0)
				{
					updatedLines.add(split.get(split.size() - 1) + "\n");
				}
			}

			List<String> split = fontRendererObj.listFormattedStringToWidth(lines.get(lines.size() - 1), width - padding * 2);
			for(int i = 0; i < split.size() - 1; i++)
			{
				updatedLines.add(split.get(i));
			}
			if(split.size() > 0)
			{
				updatedLines.add(split.get(split.size() - 1));
			}

            List<String> activeLine = fontRendererObj.listFormattedStringToWidth(lines.get(cursorY), width - padding * 2);
            int totalLength = 0;
            for(int i = 0; i < activeLine.size(); i++)
            {
                String line = activeLine.get(i);
                if(totalLength + line.length() < cursorX)
                {
                    totalLength += line.length();
                    cursorY++;
                }
                else
                {
                    cursorX -= totalLength;
                    break;
                }
            }
		}
		else
		{
		    int totalLength = 0;
			int lineIndex = 0;
			StringBuilder builder = new StringBuilder();
			do
			{
				String line = lines.get(lineIndex);
				builder.append(line);

				if(lineIndex == cursorY)
                {
                    cursorX += totalLength;
                    cursorY = updatedLines.size();
                }
                else
                {
                    totalLength += line.length();
                }

				if(!line.endsWith("\n"))
				{
					if(lineIndex == lines.size() - 1)
					{
						updatedLines.add(builder.toString());
						break;
					}
				}
				else
				{
					updatedLines.add(builder.toString());
					builder.setLength(0);
					totalLength = 0;
				}
			}
			while(++lineIndex < lines.size());
		}
		lines = updatedLines;
		recalculateMaxWidth();
	}

	private void updateHorizontalScroll()
	{
		if(!wrapText)
		{
			String line = lines.get(cursorY);
			int visibleWidth = width - padding * 2;
			int textWidth = fontRendererObj.getStringWidth(lines.get(cursorY).substring(0, cursorX));
			if(textWidth < horizontalScroll)
			{
				horizontalScroll = Math.max(0, textWidth - 1);
			}
			else if(textWidth > visibleWidth)
			{
				horizontalScroll = Math.max(0, textWidth - visibleWidth + 1);
			}
			else if(cursorX == 0)
			{
				horizontalScroll = 0;
			}
		}
	}

	private void recalculateMaxWidth()
	{
		int maxWidth = 0;
		for(String line : lines)
		{
			if(fontRendererObj.getStringWidth(line) > maxWidth)
			{
				maxWidth = fontRendererObj.getStringWidth(line);
			}
		}
		maxLineWidth = maxWidth;
	}

	private int getClosestLineIndex(int lineX, int lineY)
	{
		if(lineY >= lines.size())
		{
			return lines.get(Math.max(0, lines.size() - 1)).length();
		}
		lineY = MathHelper.clamp(lineY, 0, lines.size() - 1);
		String line = lines.get(lineY);
		int clickedCharX = fontRendererObj.trimStringToWidth(line, lineX).length();
		int nextCharX = MathHelper.clamp(clickedCharX + 1, 0, Math.max(0, line.length()));
		int clickedCharWidth = fontRendererObj.getStringWidth(line.substring(0, clickedCharX));
		int nextCharWidth = fontRendererObj.getStringWidth(line.substring(0, nextCharX));
		int clickedDistanceX = Math.abs(clickedCharWidth - lineX);
		int nextDistanceX = Math.abs(nextCharWidth - lineX - 1);
		if(Math.min(clickedDistanceX, nextDistanceX) == clickedDistanceX)
		{
			return clickedCharX;
		}
		else
		{
			return nextCharX;
		}
	}

	/**
	 * Appends text to the text area
	 *
	 * @param text the text to append
	 */
	public void writeText(String text)
	{
		String activeLine = getActiveLine();
		String head = activeLine.substring(0, cursorX);
		String tail = activeLine.substring(cursorX);
		String[] splitText = text.split("\n");
		if(splitText.length > 0)
		{
			lines.set(cursorY, head + splitText[0]);
		}
		if(splitText.length > 1)
		{
			for(int i = splitText.length - 2; i >= 1; i--)
			{
				lines.add(cursorY + 1, splitText[i]);
			}
			lines.add(cursorY + splitText.length - 1, splitText[splitText.length - 1] + tail);
		}
	}

	/**
	 * Clears the text
	 */
	public void clear()
	{
		cursorX = 0;
		cursorY = 0;
		lines.clear();
		lines.add("");
	}

	/**
	 * Sets the text for this component 
	 *
	 * @param text the text
	 */
	public void setText(String text)
	{
		lines.clear();
		String[] splitText = text.replace("\r", "").split("\n");
		for(int i = 0; i < splitText.length - 1; i++)
		{
			lines.add(splitText[i] + "\n");
		}
		lines.add(splitText[splitText.length - 1]);
	}

	/**
	 * Gets the text in the box
	 *
	 * @return the text
	 */
	public String getText()
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < lines.size() - 1; i++)
		{
			builder.append(lines.get(i)).append("\n");
		}
		builder.append(lines.get(lines.size() - 1));
		return builder.toString();
	}

	public void setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
	}

	public void setWrapText(boolean wrapText)
	{
		this.wrapText = wrapText;
		updateText();
	}

	public void setScrollBarSize(int scrollBarSize)
	{
		this.scrollBarSize = Math.max(0, scrollBarSize);
	}

	public void setHighlight(IHighlight highlight)
	{
		this.highlight = highlight;
	}

	/**
	 * Sets this text area focused. Makes it available for typing.
	 *
	 * @param isFocused whether the text area should be focused
	 */
	public void setFocused(boolean isFocused)
	{
		this.isFocused = isFocused;
	}

	/**
	 * Sets the padding for the text area
	 *
	 * @param padding the padding size
	 */
	public void setPadding(int padding)
	{
		this.padding = padding;
		this.visibleLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
	}

	/**
	 * Sets the text colour for this component
	 *
	 * @param color the text colour
	 */
	public void setTextColour(Color color)
	{
		this.textColour = color.getRGB();
	}

	/**
	 * Sets the background colour for this component
	 *
	 * @param color the background colour
	 */
	public void setBackgroundColour(Color color)
	{
		this.backgroundColour = color.getRGB();
	}

	/**
	 * Sets the border colour for this component
	 *
	 * @param color the border colour
	 */
	public void setBorderColour(Color color)
	{
		this.borderColour = color.getRGB();
	}

	/**
	 * Sets whether the user can edit the text
	 *
	 * @param editable is this component editable
	 */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	private enum ScrollBar
	{
		HORIZONTAL, VERTICAL;
	}
}