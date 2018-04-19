package com.mrcrayfish.device.programs.cray_pad;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.ApplicationNoteStash;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationCrayPad extends Application {


    @Override
    public void init() {
        //Objects
        //Layout Start
        Layout layoutMain;
        ItemList<FileObject> fileName;
        Button browse;
        Button open;
        Button newFile;
        Button save;
        //Editor
        Layout layoutEditor;
        TextArea editor;
        TextField title;

        //Init
        layoutMain = new Layout(180, 80);
        fileName = new ItemList<FileObject>(5, 5, 100, 5);
        layoutEditor = new Layout(363, 165);
        save = new Button(150 + 15 + 155, 5, "Save");
        open = new Button(110, 10, "Open");
        browse = new Button(110, 50, "Browse");
        editor = new TextArea(10, 30, 343, 125);
        title = new TextField(10, 5, 300);
        newFile = new Button(110, 30, "New");
        open.setEnabled(false);

        //Listeners
        newFile.setClickListener(new ClickListener() {
            @Override
            public void onClick(int mouseX, int mouseY, int mouseButton) {
                setCurrentLayout(layoutEditor);
            }
        });

        layoutMain.setInitListener(new InitListener() {
            @Override
            public void onInit() {
                fileName.removeAll();
                FileSystem.getApplicationFolder(ApplicationCrayPad.this, (folder, success) ->
                {
                    if (success) {
                        folder.search(file -> file.isForApplication(ApplicationCrayPad.this)).forEach(file ->
                        {
                            fileName.addItem(FileObject.fromFile(file));
                        });
                    } else {
                        //TODO error dialog
                    }
                });
            }
        });


        open.setClickListener((mouseX, mouseY, mouseButton) -> {
            editor.setText(fileName.getSelectedItem().getContent());
            setCurrentLayout(layoutEditor);
        });
        fileName.setItemClickListener(new ItemClickListener<FileObject>() {
            @Override
            public void onClick(FileObject fileObject, int index, int mouseButton) {
                open.setEnabled(true);
            }
        });
        browse.setClickListener(new ClickListener() {
            @Override
            public void onClick(int mouseX, int mouseY, int mouseButton) {
                Dialog.OpenFile dialog = new Dialog.OpenFile(ApplicationCrayPad.this);
                openDialog(dialog);
                dialog.setResponseHandler(new Dialog.ResponseHandler<File>() {
                    @Override
                    public boolean onResponse(boolean success, File file) {
                        if (success) {
                            editor.setText(file.getData().getString("content"));
                            setCurrentLayout(layoutEditor);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        });


        //Add components
        //Layout Start
        layoutMain.addComponent(open);
        layoutMain.addComponent(browse);
        layoutMain.addComponent(fileName);
        layoutEditor.addComponent(editor);
        layoutEditor.addComponent(save);
        layoutEditor.addComponent(title);
        layoutMain.addComponent(newFile);
        setCurrentLayout(layoutMain);


        save.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("title", title.getText());
            data.setString("content", editor.getText());

            Dialog.SaveFile dialog = new Dialog.SaveFile(ApplicationCrayPad.this, data);
            dialog.setFolder(getApplicationFolderPath());
            dialog.setResponseHandler((success, file) ->
            {
                title.clear();
                editor.clear();
                setCurrentLayout(layoutMain);
                return true;
            });
            openDialog(dialog);
        });
    }

        @Override
        public void load (NBTTagCompound tagCompound){

        }

        @Override
        public void save (NBTTagCompound tagCompound){

        }


    }

