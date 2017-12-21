package com.mrcrayfish.device.programs.system.task;

import javax.annotation.Nonnull;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TaskUpdateApplicationData extends Task {
	private int x, y, z;
	private String appId;
	private NBTTagCompound data;

	public TaskUpdateApplicationData() {
		super("update_application_data");
	}

	public TaskUpdateApplicationData(int x, int y, int z, @Nonnull String appId, @Nonnull NBTTagCompound data) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.appId = appId;
		this.data = data;
	}

	@Override
	public void prepareRequest(NBTTagCompound tag) {
		tag.setInteger("posX", x);
		tag.setInteger("posY", y);
		tag.setInteger("posZ", z);
		tag.setString("appId", appId);
		tag.setTag("appData", data);
	}

	@Override
	public void processRequest(NBTTagCompound tag, World world, EntityPlayer player) {
		TileEntity tileEntity = world
				.getTileEntity(new BlockPos(tag.getInteger("posX"), tag.getInteger("posY"), tag.getInteger("posZ")));
		if (tileEntity instanceof TileEntityLaptop) {
			TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
			laptop.setApplicationData(tag.getString("appId"), tag.getCompoundTag("appData"));
		}
		setSuccessful();
	}

	@Override
	public void prepareResponse(NBTTagCompound tag) {

	}

	@Override
	public void processResponse(NBTTagCompound tag) {

	}
}
