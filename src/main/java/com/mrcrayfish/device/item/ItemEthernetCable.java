package com.mrcrayfish.device.item;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
        this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TileEntityRouter)
            {
                if(!heldItem.hasTagCompound())
                {
                    sendErrorToast(player, "message.invalid_cable");
                    return EnumActionResult.SUCCESS;
                }

                TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
                Router router = tileEntityRouter.getRouter();

                NBTTagCompound tag = heldItem.getTagCompound();
                BlockPos devicePos = BlockPos.fromLong(tag.getLong("pos"));

                TileEntity tileEntity1 = world.getTileEntity(devicePos);
                if(tileEntity1 instanceof TileEntityNetworkDevice)
                {
                    TileEntityNetworkDevice tileEntityNetworkDevice = (TileEntityNetworkDevice) tileEntity1;
                    if(!router.isDeviceRegistered(tileEntityNetworkDevice))
                    {
                        if(router.addDevice(tileEntityNetworkDevice))
                        {
                            tileEntityNetworkDevice.connect(router);
                            heldItem.shrink(1);
                            if(getDistance(tileEntity1.getPos(), tileEntityRouter.getPos()) > DeviceConfig.getSignalRange())
                            {
                                sendSuccessToast(player, "message.successful_registered");
                            }
                            else
                            {
                                sendSuccessToast(player, "message.successful_connection");
                            }
                        }
                        else
                        {
                            sendErrorToast(player, "message.router_max_devices");
                        }
                    }
                    else
                    {
                        sendErrorToast(player, "message.device_already_connected");
                    }
                }
                else
                {
                    if(router.addDevice(tag.getUniqueId("id"), tag.getString("name")))
                    {
                        heldItem.shrink(1);
                        sendSuccessToast(player, "message.successful_registered");
                    }
                    else
                    {
                        sendErrorToast(player, "message.router_max_devices");
                    }
                }
                return EnumActionResult.SUCCESS;
            }

            if(tileEntity instanceof TileEntityNetworkDevice)
            {
                TileEntityNetworkDevice tileEntityNetworkDevice = (TileEntityNetworkDevice) tileEntity;
                heldItem.setTagCompound(new NBTTagCompound());
                NBTTagCompound tag = heldItem.getTagCompound();
                tag.setUniqueId("id", tileEntityNetworkDevice.getId());
                tag.setString("name", tileEntityNetworkDevice.getCustomName());
                tag.setLong("pos", tileEntityNetworkDevice.getPos().toLong());

                sendInfoToast(player, "message.select_router");
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void sendSuccessToast(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            Notification notification = new Notification(Icons.CHECK, "Success", message);
            notification.pushTo((EntityPlayerMP) player);
        }
    }

    private void sendInfoToast(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            Notification notification = new Notification(Icons.INFO, "Info", message);
            notification.pushTo((EntityPlayerMP) player);
        }
    }

    private void sendErrorToast(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            Notification notification = new Notification(Icons.ERROR, "Error", message);
            notification.pushTo((EntityPlayerMP) player);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            if(player.isSneaking())
            {
                heldItem.clearCustomName();
                heldItem.setTagCompound(null);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if(stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag != null)
            {
                tooltip.add(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "ID: " + TextFormatting.RESET.toString() + tag.getUniqueId("id"));
                tooltip.add(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Device: " + TextFormatting.RESET.toString() + tag.getString("name"));

                BlockPos devicePos = BlockPos.fromLong(tag.getLong("pos"));
                StringBuilder builder = new StringBuilder();
                builder.append(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "X: " + TextFormatting.RESET.toString() + devicePos.getX() + " ");
                builder.append(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Y: " + TextFormatting.RESET.toString() + devicePos.getY() + " ");
                builder.append(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Z: " + TextFormatting.RESET.toString() + devicePos.getZ());
                tooltip.add(builder.toString());
            }
        }
        else
        {
            if(!GuiScreen.isShiftKeyDown())
            {
                tooltip.add(TextFormatting.GRAY.toString() + "Use this cable to connect");
                tooltip.add(TextFormatting.GRAY.toString() + "a device to a router.");
                tooltip.add(TextFormatting.YELLOW.toString() + "Hold SHIFT for How-To");
                return;
            }

            tooltip.add(TextFormatting.GRAY.toString() + "Start by right clicking a");
            tooltip.add(TextFormatting.GRAY.toString() + "device with this cable");
            tooltip.add(TextFormatting.GRAY.toString() + "then right click the ");
            tooltip.add(TextFormatting.GRAY.toString() + "router you want to");
            tooltip.add(TextFormatting.GRAY.toString() + "connect this device to.");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return stack.hasTagCompound();
    }

    private static double getDistance(BlockPos source, BlockPos target)
    {
        return Math.sqrt(source.distanceSqToCenter(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if(stack.hasTagCompound())
        {
            return TextFormatting.GRAY.toString() + TextFormatting.BOLD.toString() + super.getItemStackDisplayName(stack);
        }
        return super.getItemStackDisplayName(stack);
    }
}
