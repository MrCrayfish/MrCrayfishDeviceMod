package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.entity.EntitySeat;
import com.mrcrayfish.device.object.Bounds;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import com.mrcrayfish.device.util.SeatUtil;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockOfficeChair extends BlockColorable
{
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    private static final AxisAlignedBB EMPTY_BOX = new Bounds(0, 0, 0, 0, 0, 0).toAABB();
    private static final AxisAlignedBB SELECTION_BOX = new Bounds(1, 0, 1, 15, 27, 15).toAABB();
    private static final AxisAlignedBB SEAT_BOUNDING_BOX = new Bounds(1, 0, 1, 15, 10, 15).toAABB();

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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if(Minecraft.getMinecraft().player.getRidingEntity() instanceof EntitySeat)
        {
            return EMPTY_BOX;
        }
        return SELECTION_BOX;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return SEAT_BOUNDING_BOX;
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
