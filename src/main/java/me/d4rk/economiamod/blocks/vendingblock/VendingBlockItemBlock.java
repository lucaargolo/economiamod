package me.d4rk.economiamod.blocks.vendingblock;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class VendingBlockItemBlock extends ItemBlock {

    public VendingBlockItemBlock(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.getHeldItem(hand).hasTagCompound() || !player.getHeldItem(hand).getTagCompound().getCompoundTag("BlockEntityTag").getString("owner").equals(player.getUniqueID().toString())) {
            if(worldIn.isRemote) player.sendMessage(new TextComponentString(TextFormatting.RED+I18n.format("gui.vendingblock.error.notowner")));
            return EnumActionResult.FAIL;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
