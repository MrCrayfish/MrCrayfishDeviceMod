package com.mrcrayfish.device.programs;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public class ApplicationIcons extends Application
{
    public ApplicationIcons()
    {
        this.setDefaultWidth(332);
        this.setDefaultHeight(150);
    }

    @Override
    public void init()
    {
        for(Icons icon : Icons.values())
        {
            int posX = (icon.ordinal() % 18) * 18;
            int posY = (icon.ordinal() / 18) * 18;
            Button button = new Button(5 + posX, 5 + posY, icon);
            button.setToolTip("Icon", icon.name());
            super.addComponent(button);
        }
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
