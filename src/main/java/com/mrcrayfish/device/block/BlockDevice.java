package com.mrcrayfish.device.block;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import com.mrcrayfish.device.util.IColored;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Author: MrCrayfish
 */
public abstract class BlockDevice extends BlockHorizontal
{
    protected BlockDevice(Material materialIn)
    {
        super(materialIn);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        return state.withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof TileEntityDevice)
        {
            TileEntityDevice tileEntityDevice = (TileEntityDevice) tileEntity;
            if(stack.hasDisplayName())
            {
                tileEntityDevice.setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if(!world.isRemote && !player.capabilities.isCreativeMode)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileEntityDevice)
            {
                TileEntityDevice device = (TileEntityDevice) tileEntity;

                NBTTagCompound tileEntityTag = new NBTTagCompound();
                tileEntity.writeToNBT(tileEntityTag);
                tileEntityTag.removeTag("x");
                tileEntityTag.removeTag("y");
                tileEntityTag.removeTag("z");
                tileEntityTag.removeTag("id");
                tileEntityTag.removeTag("color");

                removeTagsForDrop(tileEntityTag);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("BlockEntityTag", tileEntityTag);

                ItemStack drop;
                if(tileEntity instanceof IColored)
                {
                    drop = new ItemStack(Item.getItemFromBlock(this), 1, ((IColored)tileEntity).getColor().getMetadata());
                }
                else
                {
                    drop = new ItemStack(Item.getItemFromBlock(this));
                }
                drop.setTagCompound(compound);

                if(device.hasCustomName())
                {
                    drop.setStackDisplayName(device.getCustomName());
                }

                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    protected void removeTagsForDrop(NBTTagCompound tileEntityTag) {}

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    /**
     * Colored implementation of BlockDevice.
     */
    public static abstract class Colored extends BlockDevice
    {
        protected Colored(Material materialIn)
        {
            super(materialIn);
        }

        @Override
        public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof IColored)
            {
                drops.add(new ItemStack(Item.getItemFromBlock(this), 1, ((IColored) tileEntity).getColor().getMetadata()));
            }
        }

        @Override
        public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof IColored)
            {
                return new ItemStack(Item.getItemFromBlock(this), 1, ((IColored) tileEntity).getColor().getMetadata());
            }
            return super.getPickBlock(state, target, world, pos, player);
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
        {
            super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof IColored)
            {
                IColored colored = (IColored) tileEntity;
                colored.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
            }
        }

        @Override
        public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof IColored)
            {
                IColored colored = (IColored) tileEntity;
                state = state.withProperty(BlockColored.COLOR, colored.getColor());
            }
            return state;
        }

        @Override
        public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
        {
            if(!world.isRemote && !player.capabilities.isCreativeMode)
            {
                TileEntity tileEntity = world.getTileEntity(pos);
                if(tileEntity instanceof IColored)
                {
                    IColored colored = (IColored) tileEntity;

                    NBTTagCompound tileEntityTag = new NBTTagCompound();
                    tileEntity.writeToNBT(tileEntityTag);
                    tileEntityTag.removeTag("x");
                    tileEntityTag.removeTag("y");
                    tileEntityTag.removeTag("z");
                    tileEntityTag.removeTag("id");
                    tileEntityTag.removeTag("color");

                    removeTagsForDrop(tileEntityTag);

                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setTag("BlockEntityTag", tileEntityTag);

                    ItemStack  drop = new ItemStack(Item.getItemFromBlock(this), 1, colored.getColor().getMetadata());
                    drop.setTagCompound(compound);

                    if(tileEntity instanceof TileEntityDevice)
                    {
                        TileEntityDevice device = (TileEntityDevice) tileEntity;
                        if(device.hasCustomName())
                        {
                            drop.setStackDisplayName(device.getCustomName());
                        }
                    }

                    world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                }
            }
            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return state.getValue(FACING).getHorizontalIndex();
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, FACING, BlockColored.COLOR);
        }
    }
}
