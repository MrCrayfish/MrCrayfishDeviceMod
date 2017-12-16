package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntityLaptop extends TileEntity implements ITickable
{
	public boolean open = false;
	
	private NBTTagCompound applicationData;
	private NBTTagCompound systemData;
	private FileSystem fileSystem;

	@SideOnly(Side.CLIENT)
	public float rotation;

	@SideOnly(Side.CLIENT)
	public float prevRotation;

	@SideOnly(Side.CLIENT)
	private boolean hasExternalDrive;

	public void openClose()
	{
		open = !open;
		markDirty();
		TileEntityUtil.markBlockForUpdate(world, pos);
	}
	
	@Override
	public void update() 
	{
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
		this.open = compound.getBoolean("open");

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

		if(compound.hasKey("has_external_drive"))
		{
			this.hasExternalDrive = compound.getBoolean("has_external_drive");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setBoolean("open", open);

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
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = super.getUpdateTag();
		tag.setBoolean("open", this.open);

		if(systemData != null)
		{
			tag.setTag("system_data", systemData);
		}

		if(applicationData != null)
		{
			tag.setTag("application_data", applicationData);
		}

		tag.setBoolean("has_external_drive", getFileSystem().getAttachedDrive() != null);
		return tag;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 3, getUpdateTag());
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
}
