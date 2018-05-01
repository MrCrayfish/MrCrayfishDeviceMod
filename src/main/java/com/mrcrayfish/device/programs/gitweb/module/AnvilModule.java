package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.programs.gitweb.component.container.AnvilBox;
import com.mrcrayfish.device.programs.gitweb.component.container.ContainerBox;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class AnvilModule extends ContainerModule
{
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
