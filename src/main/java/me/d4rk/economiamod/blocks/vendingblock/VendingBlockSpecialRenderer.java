package me.d4rk.economiamod.blocks.vendingblock;

import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class VendingBlockSpecialRenderer extends TileEntitySpecialRenderer<VendingBlockTileEntity> {

    @Override
    public void render(VendingBlockTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = te.getItemStackHandler().getStackType();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.2, 0.5);
            //The code below was made by Elucent
            double boop = Minecraft.getSystemTime() / 800D;
            GlStateManager.translate(0D, Math.sin(boop % (2 * Math.PI)) * 0.065, 0D);
            GlStateManager.rotate((float) (boop * 40D % 360), 0, 1, 0);
            //The code above was made by Elucent
            if(stack.getItem() instanceof ItemBlock) GlStateManager.scale(1.5, 1.5, 1.5);
            else GlStateManager.translate(0, 0.2, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
        }
    }
}
