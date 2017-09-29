package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
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
		message.request.processRequest(message.nbt, ctx.getServerHandler().playerEntity.world, ctx.getServerHandler().playerEntity);
		return new MessageResponse(message.id, message.request);
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.id = buf.readInt();
		String name = ByteBufUtils.readUTF8String(buf);
		this.request = TaskManager.getTask(name);
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
