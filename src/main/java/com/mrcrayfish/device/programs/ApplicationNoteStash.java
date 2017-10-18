package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.io.FileSystem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.function.Predicate;

public class ApplicationNoteStash extends Application
{
	private static final Predicate<File> PREDICATE_FILE_NOTE = file -> !file.isFolder()
			&& file.getData().hasKey("title", Constants.NBT.TAG_STRING)
			&& file.getData().hasKey("content", Constants.NBT.TAG_STRING);

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
		//super("note_stash", "Note Stash");
	}

	@Override
	public void init() 
	{
		/* Main */
		
		layoutMain = new Layout(180, 80);
		layoutMain.setInitListener(() ->
		{
			notes.getItems().clear();
			FileSystem.getApplicationFolder(this, (folder, success) ->
			{
				if(success)
				{
					folder.search(file -> file.isForApplication(this)).forEach(file ->
					{
						notes.addItem(Note.fromFile(file));
					});
				}
				else
				{
					//TODO error dialog
				}
            });
		});

		notes = new ItemList<>(5, 5, 100, 5);
		notes.setItemClickListener((e, index, mouseButton) ->
		{
            btnView.setEnabled(true);
            btnDelete.setEnabled(true);
        });
		layoutMain.addComponent(notes);
		
		btnNew = new Button("New", 124, 5, 50, 20);
		btnNew.setClickListener((c, mouseButton) -> setCurrentLayout(layoutAddNote));
		layoutMain.addComponent(btnNew);
		
		btnView = new Button("View", 124, 30, 50, 20);
		btnView.setEnabled(false);
		btnView.setClickListener((c, mouseButton) ->
		{
            if(notes.getSelectedIndex() != -1)
            {
                Note note = notes.getSelectedItem();
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                setCurrentLayout(layoutViewNote);
            }
        });
		layoutMain.addComponent(btnView);
		
		btnDelete = new Button("Delete", 124, 55, 50, 20);
		btnDelete.setEnabled(false);
		btnDelete.setClickListener((c, mouseButton) ->
		{
            if(notes.getSelectedIndex() != -1)
            {
				if(notes.getSelectedIndex() != -1)
				{
					Note note = notes.getSelectedItem();
					File file = note.getSource();
					if(file != null)
					{
						file.delete((o, success) ->
						{
							if(success)
							{
								notes.removeItem(notes.getSelectedIndex());
								btnView.setEnabled(false);
								btnDelete.setEnabled(false);
							}
							else
							{
								//TODO error dialog
							}
						});
					}
					else
					{
						//TODO error dialog
					}
				}
            }
        });
		layoutMain.addComponent(btnDelete);
		
		
		/* Add Note */
		
		layoutAddNote = new Layout(180, 80);

		title = new TextField(5, 5, 114);
		layoutAddNote.addComponent(title);
		
		textArea = new TextArea(5, 25, 114, 50);
		textArea.setFocused(true);
		textArea.setPadding(2);
		layoutAddNote.addComponent(textArea);
		
		btnSave = new Button("Save", 124, 5, 50, 20);
		btnSave.setClickListener((c, mouseButton) ->
		{
            NBTTagCompound data = new NBTTagCompound();
            data.setString("title", title.getText());
            data.setString("content", textArea.getText());

            Dialog.SaveFile dialog = new Dialog.SaveFile(ApplicationNoteStash.this, data);
            dialog.setFolder(getApplicationFolderPath());
            dialog.setResponseHandler((success, file) ->
			{
                title.clear();
                textArea.clear();
                setCurrentLayout(layoutMain);
                return true;
            });
            openDialog(dialog);
        });
		layoutAddNote.addComponent(btnSave);
		
		btnCancel = new Button("Cancel", 124, 30, 50, 20);
		btnCancel.setClickListener((c, mouseButton) ->
		{
            title.clear();
            textArea.clear();
            setCurrentLayout(layoutMain);
        });
		layoutAddNote.addComponent(btnCancel);
		
		
		/* View Note */
		
		layoutViewNote = new Layout(180, 80);
		
		noteTitle = new Label("", 5, 5);
		layoutViewNote.addComponent(noteTitle);
		
		noteContent = new Text("", 5, 18, 110);
		layoutViewNote.addComponent(noteContent);
		
		btnBack = new Button("Back", 124, 5, 50, 20);
		btnBack.setClickListener((c, mouseButton) -> setCurrentLayout(layoutMain));
		layoutViewNote.addComponent(btnBack);
		
		setCurrentLayout(layoutMain);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {}

	@Override
	public void save(NBTTagCompound tagCompound) {}

	@Override
	public void onClose()
	{
		super.onClose();
		notes.removeAll();
	}

	@Override
	public boolean handleFile(File file)
	{
		if(!PREDICATE_FILE_NOTE.test(file))
			return false;

		NBTTagCompound data = file.getData();
		noteTitle.setText(data.getString("title"));
		noteContent.setText(data.getString("content"));
		setCurrentLayout(layoutViewNote);
		return true;
	}

	private static class Note
	{
		private File source;
		private String title;
		private String content;

		public Note(String title, String content)
		{
			this.title = title;
			this.content = content;
		}

		public File getSource()
		{
			return source;
		}

		public String getTitle()
		{
			return title;
		}

		public String getContent()
		{
			return content;
		}

		@Override
		public String toString()
		{
			return title;
		}

		public static Note fromFile(File file)
		{
			Note note = new Note(file.getData().getString("title"), file.getData().getString("content"));
			note.source = file;
			return note;
		}
	}
}
