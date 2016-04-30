package com.mrcrayfish.device.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendMessage implements IMessage, IMessageHandler<MessageSendMessage, IMessage> 
{
	private String message;
	
	public MessageSendMessage() {}
	
	public MessageSendMessage(String message) 
	{
		this.message = message;
	}
	
	@Override
	public IMessage onMessage(MessageSendMessage message, MessageContext ctx) 
	{
		ctx.getServerHandler().playerEntity.getName();
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, message);
	}

}
