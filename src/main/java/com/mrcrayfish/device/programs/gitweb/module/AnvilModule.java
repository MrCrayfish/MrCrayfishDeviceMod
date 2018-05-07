package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.programs.gitweb.component.container.AnvilBox;
import com.mrcrayfish.device.programs.gitweb.component.container.ContainerBox;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class AnvilModule extends ContainerModule
{
    @Override
    public String[] getOptionalData()
    {
        List<String> optionalData = Arrays.asList(super.getOptionalData());
        optionalData.add("slot-1");
        optionalData.add("slot-2");
        optionalData.add("slot-result");
        return optionalData.toArray(new String[0]);
    }

    @Override
    public int getHeight()
    {
        return AnvilBox.HEIGHT;
    }

    @Override
    public ContainerBox createContainer(Map<String, String> data)
    {
        ItemStack source = getItem(data, "slot-1");
        ItemStack addition = getItem(data, "slot-2");
        ItemStack result = getItem(data, "slot-result");
        return new AnvilBox(source, addition, result);
    }
}
