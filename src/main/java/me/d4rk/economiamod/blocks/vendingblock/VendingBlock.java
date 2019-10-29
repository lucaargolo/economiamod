package me.d4rk.economiamod.blocks.vendingblock;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.waila.WailaInfoProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;


@SuppressWarnings({"NullableProblems", "deprecation"})
public class VendingBlock extends Block implements ITileEntityProvider, WailaInfoProvider {

    public VendingBlock() {
        super(Material.ROCK);
        setUnlocalizedName(EconomiaMod.MODID + ".vendingblock");
        setRegistryName("vendingblock");

        setHardness(3.5F);
        setResistance(17.5F);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(0.0F);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(VendingBlockTileEntity.class, new VendingBlockSpecialRenderer());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new VendingBlockTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World p_onBlockPlacedBy_1_, BlockPos p_onBlockPlacedBy_2_, IBlockState p_onBlockPlacedBy_3_, EntityLivingBase p_onBlockPlacedBy_4_, ItemStack p_onBlockPlacedBy_5_) {
        TileEntity te = p_onBlockPlacedBy_1_.getTileEntity(p_onBlockPlacedBy_2_);
        if (te instanceof VendingBlockTileEntity) {
            ((VendingBlockTileEntity) te).setOwnerName(p_onBlockPlacedBy_4_.getName());
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof VendingBlockTileEntity)) {
            return false;
        }else{
            player.openGui(EconomiaMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && tileentity instanceof VendingBlockTileEntity) {
            VendingBlockTileEntity vte = (VendingBlockTileEntity) tileentity;
            for (int i = 0; i < vte.getItemStackHandler().getSlots(); ++i) {
                ItemStack itemstack = vte.getItemStackHandler().getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                }
            }
            vte.deleteItems();
            NBTTagCompound tag = new NBTTagCompound(), dataTag = new NBTTagCompound(), displayTag = new NBTTagCompound();
            NBTTagList loreTag = new NBTTagList();
            dataTag.setString("owner", vte.getOwnerUUID());
            loreTag.appendTag(new NBTTagString(vte.getOwnerName()));
            displayTag.setTag("Lore", loreTag);
            tag.setTag("BlockEntityTag", dataTag);
            tag.setTag("display", displayTag);
            ItemStack vending = new ItemStack(ItemBlock.getItemFromBlock(this), 1, 0);
            vending.setTagCompound(tag);
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), vending);
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof VendingBlockTileEntity) {
            VendingBlockTileEntity vte = (VendingBlockTileEntity) te;
            if(vte.getItemStackHandler().getStackType().isEmpty()) {
                currenttip.add(TextFormatting.RED+I18n.format("vendingblock.waila.nothing"));
            }else{
                currenttip.add(TextFormatting.GREEN+vte.getItemStackHandler().getStackType().getDisplayName());
                currenttip.add(I18n.format("gui.vendingblock.price")+": "+vte.getPrice()+I18n.format("gui.vendingblock.currency"));
            }

        }
        return currenttip;
    }
}
