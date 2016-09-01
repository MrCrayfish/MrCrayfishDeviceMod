package com.mrcrayfish.device.core;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public interface Wrappable
{
	void init(int x, int y);
	void onTick();
	void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks);
	void handleKeyTyped(char character, int code);
	void handleKeyReleased(char character, int code);
	void handleClick(int mouseX, int mouseY, int mouseButton);
	void handleDrag(int mouseX, int mouseY, int mouseButton);
	void handleRelease(int mouseX, int mouseY, int mouseButton);
	String getTitle();
	int getWidth();
	int getHeight();
	boolean isPendingLayoutUpdate();
	void clearPendingLayout();
	void updateComponents(int x, int y);
	void onClose();
}
