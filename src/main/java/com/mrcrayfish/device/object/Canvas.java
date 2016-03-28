package com.mrcrayfish.device.object;

import java.awt.Color;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.object.tools.ToolBucket;
import com.mrcrayfish.device.object.tools.ToolEraser;
import com.mrcrayfish.device.object.tools.ToolEyeDropper;
import com.mrcrayfish.device.object.tools.ToolPencil;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Canvas extends Component 
{
	private Tool currentTool;
	public static final Tool PENCIL = new ToolPencil();
	public static final Tool BUCKET = new ToolBucket();
	public static final Tool ERASER = new ToolEraser();
	public static final Tool EYE_DROPPER = new ToolEyeDropper();
	
	private int red, green, blue;
	private int currentColour = Color.BLACK.getRGB();
	
	private boolean drawing = false;
	private boolean showGrid = false;
	
	public int[][] pixels;
	public final int COLUMNS, ROWS;
	private final int PIXEL_WIDTH, PIXEL_HEIGHT;
	
	private int gridColour = new Color(200, 200, 200, 150).getRGB();
	
	public Canvas(int x, int y, int left, int top, int columns, int rows, int pixelWidth, int pixelHeight)
	{
		super(x, y, left, top);
		this.COLUMNS = columns;
		this.ROWS = rows;
		this.PIXEL_WIDTH = pixelWidth;
		this.PIXEL_HEIGHT = pixelHeight;
		this.pixels = new int[columns][rows];
		this.currentTool = PENCIL;
	}
	
	@Override
	public void init(Layout layout) 
	{
		for(int i = 0; i < COLUMNS; i++)
		{
			for(int j = 0; j < ROWS; j++)
			{
				pixels[i][j] = Integer.MAX_VALUE;
			}
		}
	}

	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive) 
	{
		drawRect(xPosition, yPosition, xPosition + COLUMNS * PIXEL_WIDTH + 2, yPosition + ROWS * PIXEL_HEIGHT + 2, Color.DARK_GRAY.getRGB());
		drawRect(xPosition + 1, yPosition + 1, xPosition + COLUMNS * PIXEL_WIDTH + 1, yPosition + ROWS * PIXEL_HEIGHT + 1, Color.WHITE.getRGB());
		for(int y = 0; y < ROWS; y++)
		{
			for(int x = 0; x < COLUMNS; x++)
			{
				int pixelX = xPosition + x * PIXEL_WIDTH + 1;
				int pixelY = yPosition + y * PIXEL_HEIGHT + 1;
				drawRect(pixelX, pixelY, pixelX + PIXEL_WIDTH, pixelY + PIXEL_HEIGHT, pixels[x][y]);
				if(showGrid)
				{
					drawRect(pixelX, pixelY, pixelX + PIXEL_WIDTH, pixelY + 1, gridColour);
					drawRect(pixelX, pixelY, pixelX + 1, pixelY + PIXEL_HEIGHT, gridColour);
				}
			}
		}
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton) 
	{
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + COLUMNS * PIXEL_WIDTH - 1;
		int endY = startY + ROWS * PIXEL_HEIGHT - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			this.drawing = true;
			int pixelX = (mouseX - startX) / PIXEL_WIDTH;
			int pixelY = (mouseY - startY) / PIXEL_HEIGHT;
			this.currentTool.handleClick(this, pixelX, pixelY);
		}
	}
	
	@Override
	public void handleRelease(int mouseX, int mouseY) 
	{
		this.drawing = false;
		
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + COLUMNS * PIXEL_WIDTH - 1;
		int endY = startY + ROWS * PIXEL_HEIGHT - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			int pixelX = (mouseX - startX) / PIXEL_WIDTH;
			int pixelY = (mouseY - startY) / PIXEL_HEIGHT;
			this.currentTool.handleRelease(this, pixelX, pixelY);
		}
	}
	
	@Override
	public void handleDrag(int mouseX, int mouseY) 
	{
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + COLUMNS * PIXEL_WIDTH - 1;
		int endY = startY + ROWS * PIXEL_HEIGHT - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			int pixelX = (mouseX - startX) / PIXEL_WIDTH;
			int pixelY = (mouseY - startY) / PIXEL_HEIGHT;
			this.currentTool.handleDrag(this, pixelX, pixelY);
		}
	}

	public void setColour(Color colour)
	{
		this.currentColour = colour.getRGB();
	}
	
	public void setColour(int colour)
	{
		this.currentColour = colour;
	}
	
	public void setRed(float red)
	{
		this.red = (int) (255 * Math.min(1.0, red));
		compileColour();
	}
	
	public void setGreen(float green)
	{
        this.green = (int) (255 * Math.min(1.0, green));
        compileColour();
	}
	
	public void setBlue(float blue)
	{
		this.blue = (int) (255 * Math.min(1.0, blue));
		compileColour();
	}
	
	public void compileColour()
	{
		 this.currentColour = ((255 & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
	}
	
	public int getCurrentColour()
	{
		return currentColour;
	}
	
	public void setCurrentTool(Tool currentTool) 
	{
		this.currentTool = currentTool;
	}
	
	public void setShowGrid(boolean showGrid) 
	{
		this.showGrid = showGrid;
	}
}
