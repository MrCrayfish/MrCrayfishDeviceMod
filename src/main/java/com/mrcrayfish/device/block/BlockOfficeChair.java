package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockOfficeChair extends BlockColorable
{
    public static final PropertyEnum TYPE = PropertyEnum.create("type", Type.class);

    public BlockOfficeChair()
    {
        super(Material.ROCK);
        this.setUnlocalizedName("office_chair");
        this.setRegistryName("office_chair");
        this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
