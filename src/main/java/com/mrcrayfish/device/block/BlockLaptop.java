package com.mrcrayfish.device.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.util.InventoryUtil;
import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLaptop extends BlockHorizontal implements ITileEntityProvider
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", Type.class);
	
	private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.6, 1.0);
	
	public BlockLaptop() 
	{
		super(Material.ANVIL);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, Type.BASE).withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
		return -1.0f;
	}
	
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		super.onBlockClicked(worldIn, pos, playerIn);
		if(playerIn.getHeldItemMainhand() == null) {
			TileEntity te = worldIn.getTileEntity(pos);
			if(te != null && te instanceof TileEntityLaptop) {
				playerIn.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(DeviceItems.laptop, 1, ((TileEntityLaptop)te).getColor().getMetadata()));
				worldIn.setBlockToAir(pos);
			}
		}
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity te = worldIn.getTileEntity(pos);
		System.out.println(te);
		if(te != null && te instanceof TileEntityLaptop) {
			EnumDyeColor col = EnumDyeColor.byMetadata(stack.getItemDamage());
			((TileEntityLaptop)te).setColor(col);
			worldIn.setBlockState(pos, state.withProperty(BlockColored.COLOR, col));
		}
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
		return COLLISION_BOX;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
		super.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
		return state.withProperty(FACING, placer.getHorizontalFacing());
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
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
					//ItemStack heldItem = playerIn.getHeldItem(hand);
					if(!InventoryUtil.isStackEmpty(heldItem) && heldItem.getItem() == DeviceItems.flash_drive)
					{
						if(!worldIn.isRemote)
						{
							if(laptop.getFileSystem().setAttachedDrive(heldItem.copy()))
							{
								heldItem.stackSize --;
								TileEntityUtil.markBlockForUpdate(worldIn, pos);
							}
							else
							{
								playerIn.addChatMessage(new TextComponentString("No more available USB slots!"));
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
							worldIn.spawnEntityInWorld(new EntityItem(worldIn, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
							TileEntityUtil.markBlockForUpdate(worldIn, pos);
						}
					}
					return true;
				}

				if(laptop.open && worldIn.isRemote)
				{
					playerIn.openGui(MrCrayfishDeviceMod.instance, Laptop.ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		return true;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) 
	{
		return state.withProperty(TYPE, Type.BASE);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityLaptop();
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(TYPE, Type.BASE);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, TYPE, BlockColored.COLOR);
	}
	
	public static enum Type implements IStringSerializable 
	{
		BASE, SCREEN;

		@Override
		public String getName() 
		{
			return name().toLowerCase();
		}
		
	}
}
