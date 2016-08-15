package com.mrcrayfish.device.network.message;

import com.mrcrayfish.device.api.TaskManager;
import com.mrcrayfish.device.api.app.task.Task;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageResponse implements IMessage, IMessageHandler<MessageResponse, IMessage> 
{
	private int id;
	private Task request;
	private NBTTagCompound nbt;
	
	public MessageResponse() {}
	
	public MessageResponse(int id, Task request) 
	{
		this.id = id;
		this.request = request;
	}
	
	@Override
	public IMessage onMessage(MessageResponse message, MessageContext ctx) 
	{
		message.request.processResponse(message.nbt);
		message.request.callback(message.nbt);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.id = buf.readInt();
		String name = ByteBufUtils.readUTF8String(buf);
		this.request = TaskManager.getTask(this.id);
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.id);
		ByteBufUtils.writeUTF8String(buf, this.request.getName());
		NBTTagCompound nbt = new NBTTagCompound();
		this.request.prepareResponse(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

}
