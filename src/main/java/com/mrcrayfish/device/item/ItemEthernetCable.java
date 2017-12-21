package com.mrcrayfish.device.item;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraft.nbt.NBTTagCompound;

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
                    sendGameInfoMessage(player, "message.invalid_cable");
                    return EnumActionResult.SUCCESS;
                }

                TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
                Router router = tileEntityRouter.getRouter();

                NBTTagCompound tag = heldItem.getTagCompound();
                BlockPos devicePos = BlockPos.fromLong(tag.getLong("pos"));

                TileEntity tileEntity1 = world.getTileEntity(devicePos);
                if(tileEntity1 instanceof TileEntityDevice)
                {
                    TileEntityDevice tileEntityDevice = (TileEntityDevice) tileEntity1;
                    if(!router.isDeviceRegistered(tileEntityDevice))
                    {
                        if(router.addDevice(tileEntityDevice))
                        {
                            tileEntityDevice.connect(router);
                            heldItem.shrink(1);
                            if(getDistance(tileEntity1.getPos(), tileEntityRouter.getPos()) > DeviceConfig.getSignalRange())
                            {
                                sendGameInfoMessage(player, "message.successful_registered");
                            }
                            else
                            {
                                sendGameInfoMessage(player, "message.successful_connection");
                            }
                        }
                        else
                        {
                            sendGameInfoMessage(player, "message.router_max_devices");
                        }
                    }
                    else
                    {
                        sendGameInfoMessage(player, "message.device_already_connected");
                    }
                }
                else
                {
                    if(router.addDevice(tag.getUniqueId("id"), tag.getString("name")))
                    {
                        heldItem.shrink(1);
                        sendGameInfoMessage(player, "message.successful_registered");
                    }
                    else
                    {
                        sendGameInfoMessage(player, "message.router_max_devices");
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
                tag.setUniqueId("id", tileEntityDevice.getId());
                tag.setString("name", tileEntityDevice.getDeviceName());
                tag.setLong("pos", tileEntityDevice.getPos().toLong());
                heldItem.setStackDisplayName(TextFormatting.GRAY.toString() + TextFormatting.BOLD.toString() + I18n.format("item.ethernet_cable.name"));

                sendGameInfoMessage(player, "message.select_router");
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void sendGameInfoMessage(EntityPlayer player, String message)
    {
        if(player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketChat(new TextComponentTranslation(message), ChatType.GAME_INFO));
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
}
