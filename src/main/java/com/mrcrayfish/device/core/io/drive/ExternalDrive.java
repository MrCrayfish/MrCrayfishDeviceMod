package com.mrcrayfish.device.core.io.drive;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive
{
    @Override
    public NBTTagCompound toTag()
    {
        return null;
    }

    @Override
    public Type getType()
    {
        return Type.EXTERNAL;
    }
}
