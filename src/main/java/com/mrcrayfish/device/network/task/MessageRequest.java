package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequest implements IMessage, IMessageHandler<MessageRequest, IMessage>
{
	private int id;
	private Task task;
	private NBTTagCompound nbt;

	public MessageRequest()
	{
	}

	public MessageRequest(int id, Task task)
	{
		this.id = id;
		this.task = task;
	}

	public int getId()
	{
		return id;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.id);
		ByteBufUtils.writeUTF8String(buf, TaskManager.getTaskName(task));
		NBTTagCompound nbt = new NBTTagCompound();
		this.task.prepareRequest(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.id = buf.readInt();
		String name = ByteBufUtils.readUTF8String(buf);
		this.task = TaskManager.getTask(name);
		this.nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public IMessage onMessage(MessageRequest message, MessageContext ctx)
	{
		message.task.processRequest(message.nbt, ctx.getServerHandler().player.world, ctx.getServerHandler().player);
		return new MessageResponse(message.id, message.task);
	}
}
