package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
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

public class TileEntityLaptop extends TileEntity implements ITickable
{
	public boolean open = false;
	
	private NBTTagCompound applicationData;
	private NBTTagCompound systemData;
	private FileSystem fileSystem;
	private EnumDyeColor color;
	private boolean dirtyColor;

	@SideOnly(Side.CLIENT)
	public float rotation;

	@SideOnly(Side.CLIENT)
	public float prevRotation;

	@SideOnly(Side.CLIENT)
	private boolean hasExternalDrive;
	
	@SideOnly(Side.CLIENT)
	private EnumDyeColor externalDriveColor;

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
		
		if(this.color == null) {
			this.color = this.world.getBlockState(this.pos).getValue(BlockColored.COLOR);
		} else if(this.dirtyColor) {
			this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockColored.COLOR, this.color), 3);
			this.dirtyColor = false;
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
		
		if(compound.hasKey("external_drive_color")) {
			this.externalDriveColor = EnumDyeColor.byMetadata(compound.getInteger("external_drive_color"));
		}

		if(compound.hasKey("color")) {
			int ord = compound.getInteger("color");
			if(ord >= 0 && ord < EnumDyeColor.values().length) {
				this.color = EnumDyeColor.values()[ord];
				this.dirtyColor = true;
			}
		}
	}
	
	public void setColor(EnumDyeColor color) {
		this.color = color;
	}
	
	public EnumDyeColor getColor() {
		return this.color;
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
		
		if(this.color != null) {
			compound.setInteger("color", this.color.ordinal());
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

		AbstractDrive drive = getFileSystem().getAttachedDrive();
		EnumDyeColor col = this.getFileSystem().getAttachedDriveColor();
		tag.setBoolean("has_external_drive", drive != null);
		if(drive != null && col != null) {
			tag.setInteger("external_drive_color", col.ordinal());
		}
		
		if(this.color != null) {
			tag.setInteger("color", this.color.ordinal());
		}
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
	
	public EnumDyeColor getExternalDriveColor() {
		return this.externalDriveColor;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != DeviceBlocks.LAPTOP || newState.getBlock() != DeviceBlocks.LAPTOP;
	}

}
