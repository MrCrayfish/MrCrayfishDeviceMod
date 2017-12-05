package com.mrcrayfish.device.object;

import com.mrcrayfish.device.api.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

import java.awt.*;
import java.util.Arrays;

public class Picture 
{
	private File source;
	private String name;
	private String author;
	public int[] pixels;
	public Size size;
	
	public Picture(String name, String author, Size size) 
	{
		this.name = name;
		this.author = author;
		this.pixels = new int[size.width * size.height];
		this.size = size;
		init();
	}

	private void init()
	{
		Arrays.fill(pixels, new Color(1.0F, 1.0F, 1.0F, 0.0F).getRGB());
	}

	public File getSource()
	{
		return source;
	}

	public String getName()
	{
		return name;
	}
	
	public String getAuthor() 
	{
		return author;
	}
	
	public int[] getPixels()
	{
		return pixels;
	}
	
	public int getWidth()
	{
		return size.width;
	}
	
	public int getHeight()
	{
		return size.height;
	}
	
	public int getPixelWidth()
	{
		return size.pixelWidth;
	}
	
	public int getPixelHeight()
	{
		return size.pixelHeight;
	}
	
	public int[] copyPixels()
	{
		int[] copiedPixels = new int[pixels.length];
		for(int i = 0; i < pixels.length; i++)
		{
			copiedPixels[i] = pixels[i];
		}
		return copiedPixels;
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
	
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setString("Name", getName());
		tagCompound.setString("Author", getAuthor());
		tagCompound.setIntArray("Pixels", pixels);
		tagCompound.setInteger("Resolution", size.width);
	}
	
	public static Picture fromFile(File file)
	{
		NBTTagCompound data = file.getData();
		Picture picture = new Picture(data.getString("Name"), data.getString("Author"), Size.getFromSize(data.getInteger("Resolution")));
		picture.source = file;
		picture.pixels = data.getIntArray("Pixels");
		return picture;
	}
	
	public static enum Size 
	{
		X16(16, 16, 8, 8), X32(32, 32, 4, 4);
		
		public int width, height;
		public int pixelWidth, pixelHeight;
		
		Size(int width, int height, int pixelWidth, int pixelHeight)
		{
			this.width = width;
			this.height = height;
			this.pixelWidth = pixelWidth;
			this.pixelHeight = pixelHeight;
		}
		
		public static Size getFromSize(int size)
		{
			if(size == 16) return X16;
			return X32;
		}
	}
}
