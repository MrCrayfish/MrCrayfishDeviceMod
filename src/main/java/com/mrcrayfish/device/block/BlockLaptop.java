package com.mrcrayfish.device.block;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.object.Bounds;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLaptop extends BlockDevice.Colored
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", Type.class);

	private static final AxisAlignedBB[] SCREEN_BOXES = new Bounds(13 * 0.0625, 0.0625, 1 * 0.0625, 1.0, 12 * 0.0625, 0.9375).getRotatedBounds();
	private static final AxisAlignedBB BODY_OPEN_BOX = new AxisAlignedBB(1 * 0.0625, 0.0, 1 * 0.0625, 13 * 0.0625, 1 * 0.0625, 15 * 0.0625);
	private static final AxisAlignedBB BODY_CLOSED_BOX = new AxisAlignedBB(1 * 0.0625, 0.0, 1 * 0.0625, 13 * 0.0625, 2 * 0.0625, 15 * 0.0625);
	private static final AxisAlignedBB SELECTION_BOX_OPEN = new AxisAlignedBB(0, 0, 0, 1, 12 * 0.0625, 1);
	private static final AxisAlignedBB SELECTION_BOX_CLOSED = new AxisAlignedBB(0, 0, 0, 1, 3 * 0.0625, 1);

	public BlockLaptop() 
	{
		super(Material.ANVIL);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, Type.BASE));
		this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
		this.setUnlocalizedName("laptop");
		this.setRegistryName("laptop");
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) 
	{
		TileEntity tileEntity = source.getTileEntity(pos);
		if(tileEntity instanceof TileEntityLaptop)
		{
			TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
			if(laptop.isOpen())
			{
				return SELECTION_BOX_OPEN;
			}
			else
			{
				return SELECTION_BOX_CLOSED;
			}
		}
		return FULL_BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity instanceof TileEntityLaptop)
		{
			TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
			if(laptop.isOpen())
			{
				Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_OPEN_BOX);
				Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, SCREEN_BOXES[state.getValue(FACING).getHorizontalIndex()]);
			}
			else
			{
				Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BODY_CLOSED_BOX);
			}
			return;
		}
		Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity instanceof TileEntityLaptop)
		{
			TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;

			if(playerIn.isSneaking())
			{
				if(!worldIn.isRemote)
				{
					laptop.openClose();
				}
			}
			else
			{
				if(side == state.getValue(FACING).rotateYCCW())
				{
					ItemStack heldItem = playerIn.getHeldItem(hand);
					if(!heldItem.isEmpty() && heldItem.getItem() == DeviceItems.FLASH_DRIVE)
					{
						if(!worldIn.isRemote)
						{
							if(laptop.getFileSystem().setAttachedDrive(heldItem.copy()))
							{
								heldItem.shrink(1);
							}
							else
							{
								playerIn.sendMessage(new TextComponentString("No more available USB slots!"));
							}
						}
						return true;
					}

					if(!worldIn.isRemote)
					{
						ItemStack stack = laptop.getFileSystem().removeAttachedDrive();
						if(stack != null)
						{
							BlockPos summonPos = pos.offset(state.getValue(FACING).rotateYCCW());
							worldIn.spawnEntity(new EntityItem(worldIn, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
							TileEntityUtil.markBlockForUpdate(worldIn, pos);
						}
					}
					return true;
				}

				if(laptop.isOpen() && worldIn.isRemote)
				{
					playerIn.openGui(MrCrayfishDeviceMod.instance, Laptop.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		return true;
	}

	@Override
	protected void removeTagsForDrop(NBTTagCompound tileEntityTag)
	{
		tileEntityTag.removeTag("open");
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) 
	{
		return super.getActualState(state, worldIn, pos).withProperty(TYPE, Type.BASE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return super.getStateFromMeta(meta).withProperty(TYPE, Type.BASE);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, TYPE, BlockColored.COLOR);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityLaptop();
	}

	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	public enum Type implements IStringSerializable
	{
		BASE, SCREEN;

		@Override
		public String getName() 
		{
			return name().toLowerCase();
		}
		
	}
}
