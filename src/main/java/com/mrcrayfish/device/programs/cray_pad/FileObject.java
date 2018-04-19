package com.mrcrayfish.device.programs.cray_pad;

import com.mrcrayfish.device.api.io.File;
import net.minecraftforge.event.world.NoteBlockEvent;

public class FileObject {

    private String name;
    private String content;
    private File fileLocation;

    public FileObject(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public File getFileLocation() {
        return fileLocation;
    }

    @Override
    public String toString() {
        return name;
    }

    public static FileObject fromFile(File file)
    {
        FileObject fileObject = new FileObject(file.getData().getString("title"), file.getData().getString("content"));
        fileObject.fileLocation = file;
        return fileObject;
    }
}
