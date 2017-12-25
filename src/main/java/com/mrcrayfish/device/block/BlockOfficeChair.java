package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import com.mrcrayfish.device.util.SeatUtil;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockOfficeChair extends BlockColorable
{
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public BlockOfficeChair()
    {
        super(Material.ROCK);
        this.setUnlocalizedName("office_chair");
        this.setRegistryName("office_chair");
        this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED).withProperty(TYPE, Type.LEGS));
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
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote)
        {
            SeatUtil.createSeatAndSit(worldIn, pos, playerIn, 0.5);
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityOfficeChair();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, BlockColored.COLOR, TYPE);
    }

    public enum Type implements IStringSerializable
    {
        LEGS, SEAT, FULL;

        @Override
        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
