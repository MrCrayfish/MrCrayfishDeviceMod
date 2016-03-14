package com.mrcrayfish.device.app;

import java.util.List;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.ProgressBar;
import com.mrcrayfish.device.app.components.Spinner;
import com.mrcrayfish.device.app.components.Text;
import com.mrcrayfish.device.app.components.TextArea;
import com.mrcrayfish.device.app.components.TextField;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.object.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ApplicationNoteStash extends Application 
{
	/* Main */
	private Layout layoutMain;
	private ItemList<Note> notes;
	private Button btnNew;
	private Button btnView;
	private Button btnDelete;
	
	/* Add Note */
	private Layout layoutAddNote;
	private TextField title;
	private TextArea textArea;
	private Button btnSave;
	private Button btnCancel;
	
	/* View Note */
	private Layout layoutViewNote;
	private Label noteTitle;
	private Text noteContent;
	private Button btnBack;

	public ApplicationNoteStash() 
	{
		super("note_stash", "Note Stash", 180, 80);
	}

	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);

		/* Main */
		layoutMain = new Layout();
		
		notes = new ItemList<Note>(x, y, 5, 5, 100, 5);
		notes.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c) {
				btnView.enabled = true;
				btnDelete.enabled = true;
			}
		});
		layoutMain.addComponent(notes);
		
		btnNew = new Button("New", x, y, 124, 5, 50, 20);
		layoutMain.addComponent(btnNew);
		
		btnView = new Button("View", x, y, 124, 30, 50, 20);
		btnView.enabled = false;
		layoutMain.addComponent(btnView);
		
		btnDelete = new Button("Delete", x, y, 124, 55, 50, 20);
		btnDelete.enabled = false;
		layoutMain.addComponent(btnDelete);
		
		
		/* Add Note */
		layoutAddNote = new Layout();

		title = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 5, 114);
		layoutAddNote.addComponent(title);
		
		textArea = new TextArea(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 25, 114, 50);
		textArea.setFocused(true);
		textArea.setPadding(2);
		layoutAddNote.addComponent(textArea);
		
		btnSave = new Button("Save", x, y, 124, 5, 50, 20);
		layoutAddNote.addComponent(btnSave);
		
		btnCancel = new Button("Cancel", x, y, 124, 30, 50, 20);
		layoutAddNote.addComponent(btnCancel);
		
		layoutViewNote = new Layout();
		
		noteTitle = new Label("", x, y, 5, 5);
		layoutViewNote.addComponent(noteTitle);
		
		noteContent = new Text("", Minecraft.getMinecraft().fontRendererObj, x, y, 5, 18, 110);
		layoutViewNote.addComponent(noteContent);
		
		btnBack = new Button("Back", x, y, 124, 5, 50, 20);
		layoutViewNote.addComponent(btnBack);
		
		this.setCurrentLayout(layoutMain);
	}

	@Override
	public void handleButtonClick(Button button)
	{
		if(button == btnNew)
		{
			this.setCurrentLayout(layoutAddNote);
		}
		else if(button == btnDelete)
		{
			if(notes.getSelectedIndex() != -1)
			{
				notes.removeItem(notes.getSelectedIndex());
				btnView.enabled = false;
				btnDelete.enabled = false;
				this.markDirty();
			}
		}
		else if(button == btnSave)
		{
			notes.addItem(new Note(title.getText(), textArea.getText()));
			this.markDirty();
			this.setCurrentLayout(layoutMain);
		}
		else if(button == btnCancel)
		{
			this.setCurrentLayout(layoutMain);
		}
		else if(button == btnView)
		{
			if(notes.getSelectedIndex() != -1)
			{
				Note note = notes.getSelectedItem();
				noteTitle.setText(note.title);
				noteContent.setText(note.content);
				this.setCurrentLayout(layoutViewNote);
			}
		}
		else if(button == btnBack)
		{
			this.setCurrentLayout(layoutMain);
		}
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		notes.getItems().clear();
		
		if(tagCompound.hasKey("Notes"))
		{
			NBTTagList noteList = (NBTTagList) tagCompound.getTag("Notes");
			for(int i = 0; i < noteList.tagCount(); i++)
			{
				Note note = Note.readFromNBT(noteList.getCompoundTagAt(i));
				notes.addItem(note);
			}
		}
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		NBTTagList noteList = new NBTTagList();
		for(int i = 0; i < notes.getItems().size(); i++)
		{
			Note note = notes.getItems().get(i);
			NBTTagCompound noteTag = new NBTTagCompound();
			noteTag.setString("Title", note.title);
			noteTag.setString("Content", note.content);
			noteList.appendTag(noteTag);
		}
		tagCompound.setTag("Notes", noteList);
	}
}
