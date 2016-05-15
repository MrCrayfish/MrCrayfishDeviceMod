package com.mrcrayfish.device.object;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonArrow;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.object.tiles.Tile.Category;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TileGrid extends Component
{
	private Label labelCurrentCategory;
	private Button btnNextCategory;
	private Button btnPrevCategory;
	
	private int currentCategory;
	private List<Tile> tabTiles;
	private Game game;
	
	public TileGrid(int x, int y, int left, int top, Game game)
	{
		super(x, y, left, top);
		this.currentCategory = 0;
		this.tabTiles = new ArrayList<Tile>();
		this.game = game;
	}
	
	@Override
	public void init(Layout layout)
	{
		labelCurrentCategory = new Label("", xPosition, yPosition, left + 14, top + 2);
		layout.addComponent(labelCurrentCategory);
		
		btnNextCategory = new ButtonArrow(xPosition, yPosition, left + 81, top, ButtonArrow.Type.RIGHT);
		btnNextCategory.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(currentCategory < Tile.Category.values().length - 1)
				{
					currentCategory++;
					updateTiles();
				}
			}
		});
		layout.addComponent(btnNextCategory);
		
		btnPrevCategory = new ButtonArrow(xPosition, yPosition, left, top, ButtonArrow.Type.LEFT);
		btnPrevCategory.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(currentCategory > 0)
				{
					currentCategory--;
					updateTiles();
				}
			}
		});
		layout.addComponent(btnPrevCategory);
		
		updateTiles();
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		drawRect(xPosition, yPosition + 15, xPosition + 93, yPosition + 100, Color.DARK_GRAY.getRGB());
		drawRect(xPosition + 1, yPosition + 16, xPosition + 92, yPosition + 99, Color.GRAY.getRGB());
		

		mc.getTextureManager().bindTexture(Game.ICONS);
		for(int i = 0; i < tabTiles.size(); i++)
		{
			Tile tile = tabTiles.get(i);
			int x = i % 6 * 15 + xPosition + 3;
			int y = i / 6 * 15 + yPosition + 18;
			if(GuiHelper.isMouseInside(mouseX, mouseY, x - 1, y - 1, x + 12, y + 12) || game.getCurrentTile() == tile)
				drawRect(x - 1, y - 1, x + 13, y + 13, Color.WHITE.getRGB());
			else
				drawRect(x - 1, y - 1, x + 13, y + 13, Color.LIGHT_GRAY.getRGB());
			GlStateManager.pushAttrib();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GuiHelper.drawModalRectWithUV(x, y, tile.x * 16, tile.y * 16, 12, 12, 16, 16);
			GlStateManager.popAttrib();
		}

		if(GuiHelper.isMouseInside(mouseX, mouseX, xPosition, yPosition, xPosition + 60, yPosition + 60))
		{
			for(int i = 0; i < tabTiles.size(); i++)
			{
				int x = i % 6 * 15 + xPosition + 2;
				int y = i / 6 * 15 + yPosition + 17;
				if(GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 14, y + 14))
				{
					drawRect(x - 1, y - 1, x + 13, y + 13, Color.WHITE.getRGB());
				}
			}
		}
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
	{
		for(int i = 0; i < tabTiles.size(); i++)
		{
			int x = i % 6 * 15 + xPosition + 3;
			int y = i / 6 * 15 + yPosition + 18;
			if(GuiHelper.isMouseInside(mouseX, mouseY, x - 1, y - 1, x + 12, y + 12))
			{
				game.setCurrentTile(tabTiles.get(i));
				return;
			}
		}
	}
	
	public void updateTiles()
	{
		tabTiles.clear();
		
		Category category = Tile.Category.values()[currentCategory];
		labelCurrentCategory.setText(category.name);
		
		for(Tile tile : Game.getRegisteredtiles().values())
		{
			if(tile.getCategory() == category)
			{
				tabTiles.add(tile);
			}
		}
	}

}
