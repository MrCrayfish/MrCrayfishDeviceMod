package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageResponse implements IMessage, IMessageHandler<MessageResponse, IMessage> {
	private int id;
	private Task request;
	private NBTTagCompound nbt;

	public MessageResponse() {
	}

	public MessageResponse(int id, Task request) {
		this.id = id;
		this.request = request;
	}

	@Override
	public IMessage onMessage(MessageResponse message, MessageContext ctx) {
		message.request.processResponse(message.nbt);
		message.request.callback(message.nbt);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		boolean successful = buf.readBoolean();
		request = TaskManager.getTaskAndRemove(id);
		if (successful) {
			request.setSuccessful();
		}
		ByteBufUtils.readUTF8String(buf);
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeBoolean(request.isSucessful());
		ByteBufUtils.writeUTF8String(buf, request.getName());
		NBTTagCompound nbt = new NBTTagCompound();
		request.prepareResponse(nbt);
		ByteBufUtils.writeTag(buf, nbt);
		request.complete();
	}

}
