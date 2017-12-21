package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.ApplicationEmail.EmailManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskCheckEmailAccount extends Task {
	private boolean hasAccount = false;
	private String name = null;

	public TaskCheckEmailAccount() {
		super("check_email_account");
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) {
		hasAccount = EmailManager.INSTANCE.hasAccount(player.getUniqueID());
		if (hasAccount) {
			name = EmailManager.INSTANCE.getName(player);
			setSuccessful();
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) {
		if (isSucessful()) {
			nbt.setString("Name", name);
		}
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {
	}

}
