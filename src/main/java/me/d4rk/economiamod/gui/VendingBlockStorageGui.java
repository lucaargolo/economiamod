package me.d4rk.economiamod.gui;

import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockStorageContainer;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class VendingBlockStorageGui extends GuiContainer {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(EconomiaMod.MODID, "textures/gui/vendingblock_storage.png");

    public VendingBlockStorageGui(VendingBlockTileEntity tileEntity, VendingBlockStorageContainer container) {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("gui.vendingblock.container"), 8, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("gui.vendingblock.player"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
