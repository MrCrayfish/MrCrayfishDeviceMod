package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.util.Colorable;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLaptop extends TileEntityDevice implements ITickable, Colorable
{
	private String name = "Laptop";
	private boolean open = false;
	private EnumDyeColor color = EnumDyeColor.RED;

	private NBTTagCompound applicationData;
	private NBTTagCompound systemData;
	private FileSystem fileSystem;

	@SideOnly(Side.CLIENT)
	public float rotation;

	@SideOnly(Side.CLIENT)
	public float prevRotation;

	@SideOnly(Side.CLIENT)
	private boolean hasExternalDrive;

	@Override
	public String getDeviceName()
	{
		return name;
	}

	@Override
	public void update() 
	{
		super.update();
		if(world.isRemote)
		{
			prevRotation = rotation;
			if(!open)
			{
				if(rotation > 0)
				{
					rotation -= 10F;
				}
			}
			else
			{
				if(rotation < 110)
				{
					rotation += 10F;
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{
		super.readFromNBT(compound);
		if(compound.hasKey("open"))
		{
			this.open = compound.getBoolean("open");
		}
		if(compound.hasKey("device_name", Constants.NBT.TAG_STRING))
		{
			this.name = compound.getString("device_name");
		}
		if(compound.hasKey("system_data", Constants.NBT.TAG_COMPOUND))
		{
			this.systemData = compound.getCompoundTag("system_data");
		}
		if(compound.hasKey("application_data", Constants.NBT.TAG_COMPOUND))
		{
			this.applicationData = compound.getCompoundTag("application_data");
		}
		if(compound.hasKey("file_system"))
		{
			this.fileSystem = new FileSystem(this, compound.getCompoundTag("file_system"));
		}
		if(compound.hasKey("has_external_drive")) //TODO fix plz validate type
		{
			this.hasExternalDrive = compound.getBoolean("has_external_drive");
		}
		if(compound.hasKey("color", Constants.NBT.TAG_BYTE))
		{
			this.color = EnumDyeColor.byDyeDamage(compound.getByte("color"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setBoolean("open", open);
		compound.setString("device_name", name);
		compound.setByte("color", (byte) color.getDyeDamage());

		if(systemData != null)
		{
			compound.setTag("system_data", systemData);
		}

		if(applicationData != null)
		{
			compound.setTag("application_data", applicationData);
		}

		if(fileSystem != null)
		{
			compound.setTag("file_system", fileSystem.toTag());
		}
		return compound;
	}

	@Override
	public NBTTagCompound writeSyncTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("open", open);
		tag.setString("device_name", name);
		tag.setBoolean("has_external_drive", getFileSystem().getAttachedDrive() != null);
		tag.setByte("color", (byte) color.getDyeDamage());
		return tag;
	}

	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 16384;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}

	public void openClose()
	{
		open = !open;
		pipeline.setBoolean("open", open);
		sync();
	}

	public boolean isOpen()
	{
		return open;
	}

	public NBTTagCompound getApplicationData()
    {
		return applicationData != null ? applicationData : new NBTTagCompound();
    }

	public NBTTagCompound getSystemData()
	{
		return systemData != null ? systemData : new NBTTagCompound();
	}

	public FileSystem getFileSystem()
	{
		if(fileSystem == null)
		{
			fileSystem = new FileSystem(this, new NBTTagCompound());
		}
		return fileSystem;
	}

	public void setApplicationData(String appId, NBTTagCompound applicationData)
	{
		this.applicationData = applicationData;
		markDirty();
		TileEntityUtil.markBlockForUpdate(world, pos);
	}

	public void setSystemData(NBTTagCompound systemData)
	{
		this.systemData = systemData;
		markDirty();
		TileEntityUtil.markBlockForUpdate(world, pos);
	}

	public boolean isExternalDriveAttached()
	{
		return hasExternalDrive;
	}

	public void setColor(EnumDyeColor color)
	{
		this.color = color;
	}

	public EnumDyeColor getColor()
	{
		return color;
	}
}
