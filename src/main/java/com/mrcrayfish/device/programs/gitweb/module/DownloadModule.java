package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.io.File;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class DownloadModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "app", "data" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return data.containsKey("text") ? 40 : 26;
    }

    @Override
    public void generate(Application app, Map<String, String> data, int width, Layout layout)
    {
        if(data.containsKey("text"))
        {
            Label label = new Label(data.get("text"), width / 2, 5);
            label.setAlignment(Component.ALIGN_CENTER);
            layout.addComponent(label);
        }

        int buttonOffset = data.containsKey("text") ? 16 : 5;
        Icons icon = Icons.valueOf(data.getOrDefault("button-icon", "IMPORT"));
        Button button = new Button(0, buttonOffset, data.getOrDefault("button-label", "Download File"), icon);
        button.left = (width - button.getWidth()) / 2;
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            try
            {
                NBTTagCompound tag = JsonToNBT.getTagFromJson(data.get("data"));
                File file = new File("", data.get("app"), tag);
                Dialog dialog = new Dialog.SaveFile(app, file);
                app.openDialog(dialog);
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        });
        layout.addComponent(button);
    }
}
