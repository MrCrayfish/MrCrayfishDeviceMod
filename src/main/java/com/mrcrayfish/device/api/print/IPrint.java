package com.mrcrayfish.device.api.print;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public interface IPrint
{
    NBTTagCompound toTag();

    void fromTag(NBTTagCompound tag);

    @SideOnly(Side.CLIENT)
    Class<? extends Renderer> getRenderer();

    interface Renderer
    {
        boolean render(NBTTagCompound data);
    }
}
