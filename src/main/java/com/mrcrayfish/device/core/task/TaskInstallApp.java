package com.mrcrayfish.device.core.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
@CDMRegister(modId = Reference.MOD_ID, uid = "system_install_app")
public class TaskInstallApp extends Task
{
    private String appId;
    private BlockPos laptopPos;
    private boolean install;

    private TaskInstallApp()
    {
        super("install_app");
    }

    public TaskInstallApp(AppInfo info, BlockPos laptopPos, boolean install)
    {
        this();
        this.appId = info.getFormattedId();
        this.laptopPos = laptopPos;
        this.install = install;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setString("appId", appId);
        nbt.setLong("pos", laptopPos.toLong());
        nbt.setBoolean("install", install);
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        String appId = nbt.getString("appId");
        TileEntity tileEntity = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            NBTTagCompound systemData = laptop.getSystemData();
            NBTTagList tagList = systemData.getTagList("InstalledApps", Constants.NBT.TAG_STRING);

            if(nbt.getBoolean("install"))
            {
                for(int i = 0; i < tagList.tagCount(); i++)
                {
                    if(tagList.getStringTagAt(i).equals(appId))
                    {
                        return;
                    }
                }
                tagList.appendTag(new NBTTagString(appId));
                this.setSuccessful();
            }
            else
            {
                for(int i = 0; i < tagList.tagCount(); i++)
                {
                    if(tagList.getStringTagAt(i).equals(appId))
                    {
                        tagList.removeTag(i);
                        this.setSuccessful();
                    }
                }
            }
            systemData.setTag("InstalledApps", tagList);
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {

    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {

    }
}
