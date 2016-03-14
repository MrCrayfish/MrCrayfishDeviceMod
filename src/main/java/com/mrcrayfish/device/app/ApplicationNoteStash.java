package com.mrcrayfish.device.app;

import java.util.List;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.TextArea;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationNoteStash extends Application 
{
	private Button btnNewNote;
	private TextArea textArea;

	public ApplicationNoteStash() 
	{
		super("note_stash", "Note Stash", 160, 80);
	}

	@Override
	public void init(int x, int y) 
	{
		textArea = new TextArea(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 5, 90, 50);
		textArea.setFocused(true);
		textArea.setPadding(2);
		this.addComponent(textArea);
		
		btnNewNote = new Button("Save", x, y, 105, 5, 50, 20);
		this.addComponent(btnNewNote);
	}
	
	@Override
	public void onTick() 
	{
		textArea.onTick();
	}

	@Override
	public void handleButtonClick(Button button)
	{
		if(button == btnNewNote)
		{
			markDirty();
			System.out.println("Marked Dirty");
		}
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
