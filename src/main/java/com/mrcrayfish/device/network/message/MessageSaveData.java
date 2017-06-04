package com.mrcrayfish.device.network.message;

import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSaveData implements IMessage, IMessageHandler<MessageSaveData, IMessage>
{
	private int x, y, z;
	private NBTTagCompound data;
	
	public MessageSaveData() {}
	
	public MessageSaveData(int x, int y, int z, NBTTagCompound data) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeTag(buf, this.data);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.data = ByteBufUtils.readTag(buf);
	}

	@Override
	public IMessage onMessage(MessageSaveData message, MessageContext ctx) 
	{
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TileEntity tileEntity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
		if(tileEntity instanceof TileEntityLaptop)
		{
			TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
			laptop.setAppData(message.data);
		}
		return null;
	}

}
