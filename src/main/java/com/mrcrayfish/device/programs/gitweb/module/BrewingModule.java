package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.programs.gitweb.component.BrewingBox;
import com.mrcrayfish.device.programs.gitweb.component.ContainerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.util.Arrays;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BrewingModule extends ContainerModule
{
    @Override
    public int getHeight()
    {
        return BrewingBox.HEIGHT;
    }

    @Override
    public ContainerBox createContainer(Map<String, String> data)
    {
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

        ItemStack[] output = new ItemStack[3];
        Arrays.fill(output, ItemStack.EMPTY);
        for(int i = 0; i < output.length; i++)
        {
            if(data.containsKey("slot-output-" + (i + 1)))
            {
                try
                {
                    output[i] = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-output-" + (i + 1))));
                }
                catch(NBTException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return new BrewingBox(fuel, input, output);
    }
}
