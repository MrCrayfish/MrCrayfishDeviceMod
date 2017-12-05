package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.object.Bounds;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import com.mrcrayfish.device.util.CollisionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockPrinter extends BlockHorizontal implements ITileEntityProvider
{
    private static final Bounds BODY_BOUNDS = new Bounds(5 * 0.0625, 0.0, 1 * 0.0625, 14 * 0.0625, 5 * 0.0625, 15 * 0.0625);
    private static final AxisAlignedBB BODY_BOX_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, BODY_BOUNDS);
    private static final AxisAlignedBB BODY_BOX_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, BODY_BOUNDS);
    private static final AxisAlignedBB BODY_BOX_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, BODY_BOUNDS);
    private static final AxisAlignedBB BODY_BOX_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, BODY_BOUNDS);
    private static final AxisAlignedBB[] BODY_BOUNDING_BOX = { BODY_BOX_SOUTH, BODY_BOX_WEST, BODY_BOX_NORTH, BODY_BOX_EAST };

    private static final Bounds TRAY_BOUNDS = new Bounds(0.5 * 0.0625, 0.0, 3.5 * 0.0625, 5 * 0.0625, 1 * 0.0625, 12.5 * 0.0625);
    private static final AxisAlignedBB TRAY_BOX_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, TRAY_BOUNDS);
    private static final AxisAlignedBB TRAY_BOX_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, TRAY_BOUNDS);
    private static final AxisAlignedBB TRAY_BOX_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, TRAY_BOUNDS);
    private static final AxisAlignedBB TRAY_BOX_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, TRAY_BOUNDS);
    private static final AxisAlignedBB[] TRAY_BOUNDING_BOX = { TRAY_BOX_SOUTH, TRAY_BOX_WEST, TRAY_BOX_NORTH, TRAY_BOX_EAST };

    private static final Bounds PAPER_BOUNDS = new Bounds(13 * 0.0625, 0.0, 4 * 0.0625, 1.0, 9 * 0.0625, 12 * 0.0625);
    private static final AxisAlignedBB PAPER_BOX_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, PAPER_BOUNDS);
    private static final AxisAlignedBB PAPER_BOX_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, PAPER_BOUNDS);
    private static final AxisAlignedBB PAPER_BOX_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, PAPER_BOUNDS);
    private static final AxisAlignedBB PAPER_BOX_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, PAPER_BOUNDS);
    private static final AxisAlignedBB[] PAPER_BOUNDING_BOX = { PAPER_BOX_SOUTH, PAPER_BOX_WEST, PAPER_BOX_NORTH, PAPER_BOX_EAST };

    private static final AxisAlignedBB SELECTION_BOUNDING_BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);

    public BlockPrinter()
    {
        super(Material.ANVIL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
        this.setUnlocalizedName("printer");
        this.setRegistryName("printer");
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SELECTION_BOUNDING_BOX;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_)
    {
        EnumFacing facing = state.getValue(FACING);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_BOUNDING_BOX[facing.getHorizontalIndex()]);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, TRAY_BOUNDING_BOX[facing.getHorizontalIndex()]);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, PAPER_BOUNDING_BOX[facing.getHorizontalIndex()]);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof TileEntityPrinter)
        {
            if(((TileEntityPrinter) tileEntity).addPaper(heldItem, playerIn.isSneaking()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if(stack.hasTagCompound())
        {
            TileEntity printer = new TileEntityPrinter();
            if(stack.hasTagCompound())
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("x", pos.getX());
                tag.setInteger("y", pos.getY());
                tag.setInteger("z", pos.getZ());
                tag.setString("id", "cdm:printer");

                if(stack.hasDisplayName())
                {
                    tag.setString("name", stack.getDisplayName());
                }

                printer.readFromNBT(tag);
                printer.validate();
                worldIn.setTileEntity(pos, printer);
            }
        }
    }


    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        return state.withProperty(FACING, placer.getHorizontalFacing());
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
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPrinter();
    }
}
