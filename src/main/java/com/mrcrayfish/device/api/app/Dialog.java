package com.mrcrayfish.device.api.app;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Wrappable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;

public class Dialog implements Wrappable
{
	private String title;
	private int width;
	private int height;

	private final Layout defaultLayout;
	private Layout customLayout;
	
	boolean pendingLayoutUpdate = true;
	
	public Dialog() 
	{
		this.defaultLayout = new Layout(100, 40);
	}
	
	protected final void addComponent(Component c)
	{
		if (c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	protected final void setLayout(Layout layout)
	{
		this.customLayout = layout;
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
		this.customLayout.init();
	}
	
	@Override
	public void init(int x, int y)
	{
		this.defaultLayout.clear();
		this.setLayout(defaultLayout);
	}

	@Override
	public void onTick()
	{
		for (Component c : customLayout.components)
		{
			c.handleTick();
		}
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		customLayout.render(laptop, mc, x, y);

		for (Component c : customLayout.components)
		{
			c.render(laptop, mc, mouseX, mouseY, active, partialTicks);
		}

		for (Component c : customLayout.components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, active);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void handleClick(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleDrag(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleDrag(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleRelease(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleRelease(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleKeyTyped(char character, int code)
	{
		for (Component c : customLayout.components)
		{
			c.handleKeyTyped(character, code);
		}
	}

	@Override
	public String getTitle()
	{
		return "Message";
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public boolean isPendingLayoutUpdate()
	{
		return pendingLayoutUpdate;
	}

	@Override
	public void clearPendingLayout()
	{
		this.pendingLayoutUpdate = false;
	}

	@Override
	public void updateComponents(int x, int y)
	{
		for (Component c : customLayout.components)
		{
			c.updateComponents(x, y);
		}
	}

	@Override
	public void onClose()
	{
		this.customLayout = null;
	}
	
	public static Dialog newConfirmationDialog()
	{
		Dialog dialog = new Dialog() 
		{
			@Override
			public void init(int x, int y)
			{
				super.init(x, y);
				Label label = new Label("Are you sure?", x, y, 5, 5);
				this.addComponent(label);
			}
		};
		return dialog;
	}

	public static class Builder
	{
		
	}

}
