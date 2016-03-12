package com.mrcrayfish.device.network;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.network.message.MessageSaveData;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	public static void init()
	{
		INSTANCE.registerMessage(MessageSaveData.class, MessageSaveData.class, 0, Side.SERVER);
	}
}
