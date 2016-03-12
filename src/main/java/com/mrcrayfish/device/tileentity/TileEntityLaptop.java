package com.mrcrayfish.device.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLaptop extends TileEntity implements ITickable
{
	public float rotation = 0;
	public float prevRotation = 0;
	public boolean open = false;
	
	private NBTTagCompound data;
	
	public TileEntityLaptop() 
	{
		this.data = new NBTTagCompound();
	}
	
	public NBTTagCompound getAppData() 
	{
		return data;
	}
	
	public void setAppData(NBTTagCompound data) 
	{
		this.data = data;
		worldObj.markBlockForUpdate(pos);
		markDirty();
	}
	
	public void openClose()
	{
		open = !open;
		worldObj.markBlockForUpdate(pos);
	}
	
	@Override
	public void update() 
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
	
	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{
		super.readFromNBT(compound);
		this.open = compound.getBoolean("open");
		this.data = compound.getCompoundTag("AppData");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setBoolean("open", open);
		compound.setTag("AppData", data);
		System.out.println("Writing");
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound tagCom = pkt.getNbtCompound();
		this.readFromNBT(tagCom);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tagCom = new NBTTagCompound();
		this.writeToNBT(tagCom);
		return new S35PacketUpdateTileEntity(pos, getBlockMetadata(), tagCom);
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
}
