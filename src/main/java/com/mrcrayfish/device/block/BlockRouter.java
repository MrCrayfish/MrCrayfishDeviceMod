package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageSyncBlock;
import com.mrcrayfish.device.object.Bounds;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import com.mrcrayfish.device.util.CollisionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class BlockRouter extends BlockHorizontal implements ITileEntityProvider
{
    public static final PropertyBool VERTICAL = PropertyBool.create("vertical");

    private static final AxisAlignedBB[] BODY_BOUNDING_BOX = new Bounds(4, 0, 2, 12, 2, 14).getRotatedBounds();
    private static final AxisAlignedBB[] BODY_VERTICAL_BOUNDING_BOX = new Bounds(14, 1, 2, 16, 9, 14).getRotatedBounds();
    private static final AxisAlignedBB[] SELECTION_BOUNDING_BOX = new Bounds(3, 0, 1, 13, 3, 15).getRotatedBounds();
    private static final AxisAlignedBB[] SELECTION_VERTICAL_BOUNDING_BOX = new Bounds(13, 0, 1, 16, 10, 15).getRotatedBounds();

    public BlockRouter()
    {
        super(Material.ANVIL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VERTICAL, false));
        this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
        this.setUnlocalizedName("router");
        this.setRegistryName("router");
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
        if(state.getValue(VERTICAL))
        {
            return SELECTION_VERTICAL_BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
        }
        return SELECTION_BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
        if(state.getValue(VERTICAL))
        {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_VERTICAL_BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()]);
        }
        else
        {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()]);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(worldIn.isRemote && playerIn.capabilities.isCreativeMode)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityRouter)
            {
                TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
                tileEntityRouter.setDebug();
                if(tileEntityRouter.isDebug())
                {
                    PacketHandler.INSTANCE.sendToServer(new MessageSyncBlock(pos));
                }
            }
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if(!world.isRemote && !player.capabilities.isCreativeMode)
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileEntityRouter)
            {
                TileEntityRouter router = (TileEntityRouter) tileEntity;

                NBTTagCompound tileEntityTag = new NBTTagCompound();
                router.writeToNBT(tileEntityTag);
                tileEntityTag.removeTag("x");
                tileEntityTag.removeTag("y");
                tileEntityTag.removeTag("z");
                tileEntityTag.removeTag("id");

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("BlockEntityTag", tileEntityTag);

                ItemStack drop = new ItemStack(Item.getItemFromBlock(this));
                drop.setTagCompound(compound);

                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        return state.withProperty(FACING, placer.getHorizontalFacing()).withProperty(VERTICAL, facing.getHorizontalIndex() != -1);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.DOWN;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityRouter();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(VERTICAL) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(VERTICAL, meta - 4 >= 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, VERTICAL);
    }
}
