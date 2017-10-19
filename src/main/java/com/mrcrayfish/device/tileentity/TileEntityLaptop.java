package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLaptop extends TileEntity implements ITickable
{
	public boolean open = false;
	
	private NBTTagCompound data;
	private FileSystem fileSystem;

	@SideOnly(Side.CLIENT)
	public float rotation = 0;

	@SideOnly(Side.CLIENT)
	public float prevRotation = 0;

	@SideOnly(Side.CLIENT)
	private boolean hasExternalDrive = false;
	
	private EnumDyeColor color = EnumDyeColor.WHITE;

	public TileEntityLaptop()
	{
		this.data = new NBTTagCompound();
	}

	public void openClose()
	{
		open = !open;
		markDirty();
		TileEntityUtil.markBlockForUpdate(worldObj, pos);
	}
	
	public void setColor(EnumDyeColor color) {
		this.color = color;
	}
	
	public EnumDyeColor getColor() {
		return this.color;
	}
	
	@Override
	public void update() 
	{
		if(worldObj.isRemote)
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
		this.data = compound.getCompoundTag("data");

		if(compound.hasKey("file_system"))
		{
			this.fileSystem = new FileSystem(this, compound.getCompoundTag("file_system"));
		}

		if(compound.hasKey("has_external_drive"))
		{
			this.hasExternalDrive = compound.getBoolean("has_external_drive");
		}
		
		if(compound.hasKey("color")) {
			this.color = EnumDyeColor.byMetadata(compound.getInteger("color"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setBoolean("open", open);
		compound.setTag("data", data);
		if(fileSystem != null) {
			compound.setTag("file_system", fileSystem.toTag());
		}
		compound.setInteger("color", this.color.getMetadata());
		return compound;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound tag = pkt.getNbtCompound();
		super.readFromNBT(tag);
		this.open = tag.getBoolean("open");
		this.data.setTag("application", tag.getCompoundTag("application"));
		this.data.setTag("system", tag.getCompoundTag("system"));
		this.hasExternalDrive = tag.getBoolean("has_external_drive");
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = super.getUpdateTag();
		tag.setBoolean("open", this.open);
		tag.setTag("application", createAndGetTag("application"));
		tag.setTag("system", createAndGetTag("system"));
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
        return createAndGetTag("application");
    }

	public NBTTagCompound getSystemData()
	{
		return createAndGetTag("system");
	}

	public FileSystem getFileSystem()
	{
		if(fileSystem == null)
		{
			fileSystem = new FileSystem(this, createAndGetTag("file_system"));
		}
		return fileSystem;
	}

	public void setApplicationData(String appId, NBTTagCompound appData)
	{
		createAndGetTag("application").setTag(appId, appData);
		markDirty();
	}

	public void setSystemData(NBTTagCompound systemData)
	{
		data.setTag("system", systemData);
		markDirty();
	}

	private NBTTagCompound createAndGetTag(String name)
	{
		if(!data.hasKey(name))
		{
			data.setTag(name, new NBTTagCompound());
			markDirty();
		}
		return data.getCompoundTag(name);
	}

	public boolean isExternalDriveAttached()
	{
		return hasExternalDrive;
	}
}
