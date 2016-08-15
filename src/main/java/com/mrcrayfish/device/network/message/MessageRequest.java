package com.mrcrayfish.device.network.message;

import com.mrcrayfish.device.api.TaskManager;
import com.mrcrayfish.device.api.app.task.Task;
import com.mrcrayfish.device.network.PacketHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequest implements IMessage, IMessageHandler<MessageRequest, IMessage> 
{
	private int id;
	private Task request;
	private NBTTagCompound nbt;
	
	public MessageRequest() {}
	
	public MessageRequest(int id, Task request) 
	{
		this.id = id;
		this.request = request;
	}
	
	public int getId() 
	{
		return id;
	}
	
	@Override
	public IMessage onMessage(MessageRequest message, MessageContext ctx) 
	{
		message.request.processRequest(message.nbt, ctx.getServerHandler().playerEntity.worldObj, ctx.getServerHandler().playerEntity);
		PacketHandler.INSTANCE.sendTo(new MessageResponse(message.id, message.request), ctx.getServerHandler().playerEntity);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.id = buf.readInt();
		String name = ByteBufUtils.readUTF8String(buf);
		this.request = TaskManager.getRequest(name);
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.id);
		ByteBufUtils.writeUTF8String(buf, this.request.getName());
		NBTTagCompound nbt = new NBTTagCompound();
		this.request.prepareRequest(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

}
