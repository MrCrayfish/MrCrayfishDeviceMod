package com.mrcrayfish.device.programs.system.task;

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
        int amount = nbt.getInteger("amount");
        if(InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount))
        {
            Account account = BankUtil.INSTANCE.getAccount(player);
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
