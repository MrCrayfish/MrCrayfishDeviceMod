package com.mrcrayfish.device.programs.auction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mrcrayfish.device.programs.auction.object.AuctionItem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AuctionManager
{
	public static final AuctionManager INSTANCE = new AuctionManager();
	
	private List<AuctionItem> items;
	
	private AuctionManager() 
	{
		items = new ArrayList<AuctionItem>();
	}
	
	public void addItem(AuctionItem item)
	{
		if(!containsItem(item.getId()))
		{
			items.add(item);
		}
	}
	
	public boolean containsItem(UUID id)
	{
		for(AuctionItem item : items)
		{
			if(item.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public AuctionItem getItem(UUID uuid)
	{
		for(AuctionItem item : items)
		{
			if(item.getId().equals(uuid))
			{
				return item;
			}
		}
		return null;
	}
	
	public void removeItem(UUID uuid)
	{
		for(AuctionItem item : items)
		{
			if(item.getId().equals(uuid))
			{
				items.remove(item);
				return;
			}
		}
	}
	
	public List<AuctionItem> getItems()
	{
		return items;
	}
	
	public void tick()
	{
		for(AuctionItem item : items)
		{
			item.decrementTime();
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagList tagList = new NBTTagList();
		items.stream().filter(i -> i.isValid()).forEach(i -> {
			NBTTagCompound itemTag = new NBTTagCompound();
			i.writeToNBT(itemTag);
			tagList.appendTag(itemTag);
		});
		tag.setTag("auctionItems", tagList);
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		items.clear();
		
		NBTTagList tagList = (NBTTagList) tag.getTag("auctionItems");
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
			AuctionItem item = AuctionItem.readFromNBT(itemTag);
			items.add(item);
		}
	}

	public List<AuctionItem> getItemsForSeller(UUID seller)
	{
		return items.stream().filter(i -> i.getSellerId().equals(seller)).collect(Collectors.toList());
	}
}
