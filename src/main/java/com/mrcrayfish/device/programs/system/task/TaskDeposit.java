package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import com.mrcrayfish.device.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
@CDMRegister(modId = Reference.MOD_ID, uid = "bank_deposit")
public class TaskDeposit extends Task
{
    private int amount;

    protected TaskDeposit()
    {
        super("bank_deposit");
    }

    public TaskDeposit(int amount)
    {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setInteger("amount", this.amount);
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        Account account = BankUtil.INSTANCE.getAccount(player);
        int amount = nbt.getInteger("amount");
        long value = account.getBalance() + amount;
        if(value > Integer.MAX_VALUE)
        {
            amount = Integer.MAX_VALUE - account.getBalance();
        }
        if(InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount))
        {
            if(account.deposit(amount))
            {
                this.amount = account.getBalance();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {
        nbt.setInteger("balance", this.amount);
    }

    @Override
    public void processResponse(NBTTagCompound nbt) {}
}
