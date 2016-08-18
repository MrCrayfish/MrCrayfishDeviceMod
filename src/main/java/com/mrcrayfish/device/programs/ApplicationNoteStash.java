package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.object.Note;

import net.minecraft.client.Minecraft;
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
		super("note_stash", "Note Stash", TaskBar.APP_BAR_GUI, 42, 30);
	}

	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);

		/* Main */
		
		layoutMain = new Layout(180, 80);
		
		notes = new ItemList<Note>(x, y, 5, 5, 100, 5);
		notes.setItemClickListener(new ItemClickListener() {
			@Override
			public void onClick(Object e, int index){
				btnView.setEnabled(true);
				btnDelete.setEnabled(true);
			}
		});
		layoutMain.addComponent(notes);
		
		btnNew = new Button("New", x, y, 124, 5, 50, 20);
		btnNew.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				setCurrentLayout(layoutAddNote);
			}
		});
		layoutMain.addComponent(btnNew);
		
		btnView = new Button("View", x, y, 124, 30, 50, 20);
		btnView.setEnabled(false);
		btnView.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(notes.getSelectedIndex() != -1)
				{
					Note note = notes.getSelectedItem();
					noteTitle.setText(note.title);
					noteContent.setText(note.content);
					setCurrentLayout(layoutViewNote);
				}
			}
		});
		layoutMain.addComponent(btnView);
		
		btnDelete = new Button("Delete", x, y, 124, 55, 50, 20);
		btnDelete.setEnabled(false);
		btnDelete.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(notes.getSelectedIndex() != -1)
				{
					notes.removeItem(notes.getSelectedIndex());
					btnView.setEnabled(false);
					btnDelete.setEnabled(false);
					markDirty();
				}
			}
		});
		layoutMain.addComponent(btnDelete);
		
		
		/* Add Note */
		
		layoutAddNote = new Layout(180, 80);

		title = new TextField(x, y, 5, 5, 114);
		layoutAddNote.addComponent(title);
		
		textArea = new TextArea(x, y, 5, 25, 114, 50);
		textArea.setFocused(true);
		textArea.setPadding(2);
		layoutAddNote.addComponent(textArea);
		
		btnSave = new Button("Save", x, y, 124, 5, 50, 20);
		btnSave.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				notes.addItem(new Note(title.getText(), textArea.getText()));
				title.clear();
				textArea.clear();
				markDirty();
				setCurrentLayout(layoutMain);
			}
		});
		layoutAddNote.addComponent(btnSave);
		
		btnCancel = new Button("Cancel", x, y, 124, 30, 50, 20);
		btnCancel.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				title.clear();
				textArea.clear();
				setCurrentLayout(layoutMain);
			}
		});
		layoutAddNote.addComponent(btnCancel);
		
		
		/* View Note */
		
		layoutViewNote = new Layout(180, 80);
		
		noteTitle = new Label("", x, y, 5, 5);
		layoutViewNote.addComponent(noteTitle);
		
		noteContent = new Text("", x, y, 5, 18, 110);
		layoutViewNote.addComponent(noteContent);
		
		btnBack = new Button("Back", x, y, 124, 5, 50, 20);
		btnBack.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				setCurrentLayout(layoutMain);
			}
		});
		layoutViewNote.addComponent(btnBack);
		
		setCurrentLayout(layoutMain);
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
