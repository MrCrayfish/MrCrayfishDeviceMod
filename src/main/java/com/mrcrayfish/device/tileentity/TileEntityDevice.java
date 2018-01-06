package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.core.network.Connection;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityDevice extends TileEntitySync implements ITickable
{
    private UUID deviceId;
    private String name;

    public final UUID getId()
    {
        if(deviceId == null)
        {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    public abstract String getDeviceName();

    public String getCustomName()
    {
        return hasCustomName() ? name : getDeviceName();
    }

    public void setCustomName(String name)
    {
       this.name = name;
    }

    public boolean hasCustomName()
    {
        return !StringUtils.isEmpty(name);
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(getCustomName());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString("deviceId", getId().toString());
        if(hasCustomName())
        {
            compound.setString("name", name);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("deviceId", Constants.NBT.TAG_STRING))
        {
            deviceId = UUID.fromString(compound.getString("deviceId"));
        }
        if(compound.hasKey("name", Constants.NBT.TAG_STRING))
        {
            name = compound.getString("name");
        }
    }

    @Override
    public NBTTagCompound writeSyncTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        if(hasCustomName())
        {
            tag.setString("name", name);
        }
        return tag;
    }
}
