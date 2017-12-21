package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRequest implements IMessage, IMessageHandler<MessageRequest, IMessage> {
	private int id;
	private Task request;
	private NBTTagCompound nbt;

	public MessageRequest() {
	}

	public MessageRequest(int id, Task request) {
		this.id = id;
		this.request = request;
	}

	public int getId() {
		return id;
	}

	@Override
	public IMessage onMessage(MessageRequest message, MessageContext ctx) {
		message.request.processRequest(message.nbt, ctx.getServerHandler().player.world, ctx.getServerHandler().player);
		return new MessageResponse(message.id, message.request);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		String name = ByteBufUtils.readUTF8String(buf);
		request = TaskManager.getTask(name);
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		ByteBufUtils.writeUTF8String(buf, request.getName());
		NBTTagCompound nbt = new NBTTagCompound();
		request.prepareRequest(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

}
