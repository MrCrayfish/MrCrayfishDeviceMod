package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.network.task.TaskGetDevices;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class ApplicationTest extends Application
{
    @Override
    public void init()
    {
        Button button = new Button(5, 5, "Print", Icons.PRINTER);
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                int[] pixels = {Color.RED.getRGB(), Color.DARK_GRAY.getRGB(), Color.YELLOW.getRGB(), Color.MAGENTA.getRGB()};
                Dialog.Print dialog = new Dialog.Print(new ApplicationPixelPainter.PicturePrint("Test", pixels, 2));
                openDialog(dialog);
            }
        });
        super.addComponent(button);

        Button getDevices = new Button(50, 5, "Get Devices", Icons.NETWORK_DRIVE);
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                Task task = new TaskGetDevices(Laptop.getPos(), TileEntityPrinter.class);
                task.setCallback((tagCompound, success) ->
                {
                    if(success)
                    {
                        System.out.println(tagCompound);
                    }
                });
                TaskManager.sendTask(task);
            }
        });
        super.addComponent(getDevices);
    }

    @Override
    public void load(NBTTagCompound tagCompound)
    {

    }

    @Override
    public void save(NBTTagCompound tagCompound)
    {

    }
}
