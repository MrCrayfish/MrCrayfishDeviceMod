package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.programs.gitweb.component.ContainerBox;
import com.mrcrayfish.device.programs.gitweb.component.FurnaceBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FurnaceModule extends ContainerModule
{
    @Override
    public int getHeight()
    {
        return FurnaceBox.HEIGHT;
    }

    @Override
    public ContainerBox createContainer(Map<String, String> data)
    {
        ItemStack input = ItemStack.EMPTY;
        if(data.containsKey("slot-input"))
        {
            try
            {
                input = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-input")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        ItemStack fuel = ItemStack.EMPTY;
        if(data.containsKey("slot-fuel"))
        {
            try
            {
                fuel = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-fuel")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if(data.containsKey("slot-result"))
        {
            try
            {
                result = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-result")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        return new FurnaceBox(input, fuel, result);
    }
}
