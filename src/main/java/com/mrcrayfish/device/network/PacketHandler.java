package com.mrcrayfish.device.network;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.network.task.MessageRequest;
import com.mrcrayfish.device.network.task.MessageResponse;
import com.mrcrayfish.device.network.task.MessageSyncApplications;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	public static void init()
	{
		INSTANCE.registerMessage(MessageRequest.class, MessageRequest.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageResponse.class, MessageResponse.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncApplications.class, MessageSyncApplications.class, 3, Side.CLIENT);
	}
}
