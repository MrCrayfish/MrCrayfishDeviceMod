package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemEthernetCable extends Item
{
    public ItemEthernetCable()
    {
        this.setUnlocalizedName("ethernet_cable");
        this.setRegistryName("ethernet_cable");
        this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TileEntityRouter && heldItem.hasTagCompound())
            {
                TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
                Router router = tileEntityRouter.getRouter();

                NBTTagCompound tag = heldItem.getTagCompound();
                BlockPos devicePos = BlockPos.fromLong(tag.getLong("pos"));

                TileEntity tileEntity1 = world.getTileEntity(devicePos);
                if(tileEntity1 instanceof TileEntityDevice)
                {
                    TileEntityDevice tileEntityDevice = (TileEntityDevice) tileEntity1;
                    if(!router.hasDevice(tileEntityDevice) && router.addDevice(tileEntityDevice))
                    {
                        tileEntityDevice.connect(router);
                        heldItem.shrink(1);
                    }
                }
                return EnumActionResult.SUCCESS;
            }

            if(tileEntity instanceof TileEntityDevice)
            {
                TileEntityDevice tileEntityDevice = (TileEntityDevice) tileEntity;
                if(!heldItem.hasTagCompound())
                {
                    heldItem.setTagCompound(new NBTTagCompound());
                }
                NBTTagCompound tag = heldItem.getTagCompound();
                tag.setLong("pos", tileEntityDevice.getPos().toLong());
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag != null)
            {
                BlockPos devicePos = BlockPos.fromLong(tag.getLong("pos"));
                StringBuilder builder = new StringBuilder();
                builder.append(TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() + "X: " + TextFormatting.RESET.toString() + devicePos.getX() + " ");
                builder.append(TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() + "Y: " + TextFormatting.RESET.toString() + devicePos.getY() + " ");
                builder.append(TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() + "Z: " + TextFormatting.RESET.toString() + devicePos.getZ());
                tooltip.add(builder.toString());
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("pos", Constants.NBT.TAG_LONG);
    }
}
