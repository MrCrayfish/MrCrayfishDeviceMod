package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.app.components.Application;
import com.mrcrayfish.device.gui.GuiLaptop;
import com.mrcrayfish.device.gui.GuiTextArea;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class ApplicationNoteStash extends Application 
{
	private GuiButton btnNewNote;
	private GuiTextArea textArea;

	public ApplicationNoteStash() 
	{
		super("note_stash", "Note Stash", 160, 80);
	}

	@Override
	public void init(List<GuiButton> buttons, int x, int y) 
	{
		textArea = new GuiTextArea(Minecraft.getMinecraft().fontRendererObj, x + 5, y + 5, 90, 50);
		textArea.setFocused(true);
		textArea.setPadding(2);
		btnNewNote = new GuiButton(0, x + 105, y + 5, 50, 20, "Save");
		buttons.add(btnNewNote);
	}
	
	@Override
	public void onTick() 
	{
		textArea.onTick();
	}
	
	@Override
	public void render(Gui gui, Minecraft mc, int x, int y) 
	{
		textArea.draw();
	}

	@Override
	public void handleClick(Gui gui, int mouseX, int mouseY, int mouseButton) 
	{
		textArea.onMouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleKeyTyped(char character, int code) 
	{
		textArea.onKeyTyped(character, code);
	}
	
	@Override
	public void handleButtonClick(GuiButton button)
	{
		if(button == btnNewNote)
		{
			markDirty();
			System.out.println("Marked Dirty");
		}
	}

	@Override
	public void hideButtons(List<GuiButton> buttons) 
	{
		buttons.remove(btnNewNote);
	}

	@Override
	public void updateButtons(int x, int y) 
	{
		btnNewNote.xPosition = x + 105;
		btnNewNote.yPosition = y + 5;
		textArea.xPosition = x + 5;
		textArea.yPosition = y + 5;
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		textArea.setText(tagCompound.getString("CurrentText"));
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		tagCompound.setString("CurrentText", textArea.getText());
	}
}
