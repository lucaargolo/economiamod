package me.d4rk.economiamod.gui;

import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.Utils;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import me.d4rk.economiamod.network.PacketHandler;
import me.d4rk.economiamod.network.PacketOpenContainer;
import me.d4rk.economiamod.network.PacketPurchase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class VendingBlockNormalGui extends GuiScreen {

    private int left, top;

    private static final ResourceLocation background = new ResourceLocation(EconomiaMod.MODID, "textures/gui/vendingblock.png");

    private int buyQnt = 1;
    private GuiButton storageBtn, configBtn, buyBtn, plusBtn, minusBtn;

    private Utils.ResultType resultType = Utils.ResultType.NONE;
    private long lastResultUpdate = System.currentTimeMillis();

    private final VendingBlockTileEntity vendingTE;
    
    public VendingBlockNormalGui(VendingBlockTileEntity vendingBlockTileEntity) {
        this.vendingTE = vendingBlockTileEntity;
    }

    public void receiveResponse(Utils.ResultType result) {
        resultType = result;
        lastResultUpdate = System.currentTimeMillis();
        if(buyQnt > vendingTE.getItemStackHandler().getItemQuantity()) buyQnt = vendingTE.getItemStackHandler().getItemQuantity();
    }

    @Override
    public void initGui() {
        left = ((this.width - 200) / 2);
        top = ((this.height - 145) / 2);
        storageBtn = this.addButton(new GuiButton(0, left+65, top+17, 65, 20, I18n.format("gui.vendingblock.storage")));
        configBtn = this.addButton(new GuiButton(1, left+130, top+17, 65, 20, I18n.format("gui.vendingblock.options")));
        minusBtn = this.addButton(new GuiButton(4, left+7, top+80, 20, 20, "-"));
        plusBtn = this.addButton(new GuiButton(3, left+173, top+80, 20, 20, "+"));
        buyBtn = this.addButton(new GuiButton(2, left+50, top+105, 100, 20, I18n.format("gui.vendingblock.purchase")));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled) {
            if(button == buyBtn) {
                PacketHandler.INSTANCE.sendToServer(new PacketPurchase(vendingTE, buyQnt));
            }else if(button == storageBtn) {
                if(mc.player.getUniqueID().toString().equals(vendingTE.getOwnerUUID())) {
                    PacketHandler.INSTANCE.sendToServer(new PacketOpenContainer(vendingTE));
                }else {
                    lastResultUpdate = System.currentTimeMillis();
                    resultType = Utils.ResultType.NOTOWNER;
                }
            }else if(button == configBtn) {
                if(mc.player.getUniqueID().toString().equals(vendingTE.getOwnerUUID())) {
                    mc.player.openGui(EconomiaMod.instance, 2, vendingTE.getWorld(), vendingTE.getPos().getX(), vendingTE.getPos().getY(), vendingTE.getPos().getZ());
                }else {
                    lastResultUpdate = System.currentTimeMillis();
                    resultType = Utils.ResultType.NOTOWNER;
                }
            }else if(button == plusBtn && buyQnt < vendingTE.getItemStackHandler().getItemQuantity()) {
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    buyQnt += 10;
                    if(buyQnt > vendingTE.getItemStackHandler().getItemQuantity()) buyQnt = vendingTE.getItemStackHandler().getItemQuantity();
                }else{
                    buyQnt++;
                }
            }else if(button == minusBtn && buyQnt > 1) {
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    buyQnt -= 10;
                    if(buyQnt < 1) buyQnt = 1;
                }else{
                    buyQnt--;
                }
            }
        }
    }

    private void drawButtons(int mouseX, int mouseY, float partialTicks) {
        storageBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        configBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        buyBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        plusBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        minusBtn.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private void drawStrings() {
        this.fontRenderer.drawString(I18n.format("gui.vendingblock.seller")+": "+vendingTE.getOwnerName(), left+65, top+41, 4210752);
        this.fontRenderer.drawString(I18n.format("gui.vendingblock.available")+": "+ vendingTE.getItemStackHandler().getItemQuantity(), left+65, top+51, 4210752);
        this.fontRenderer.drawString(I18n.format("gui.vendingblock.price")+": "+ vendingTE.getPrice() + I18n.format("gui.vendingblock.currency"), left+65, top+61, 4210752);
        drawResultString();
        //Macumba para renderizar string centered
        String centered = I18n.format("gui.vendingblock.name");
        this.fontRenderer.drawString(centered, (this.width/2) - this.fontRenderer.getStringWidth(centered) / 2,top+6, 4210752);
        //Reduce the size of the text
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75, 0.75, 0.75);
        centered = I18n.format("gui.vendingblock.buying1")+" "+buyQnt+" "+I18n.format("gui.vendingblock.buying2");
        this.fontRenderer.drawString(centered, (int)(((this.width/2)*1.33333) - this.fontRenderer.getStringWidth(centered) / 2), (int)((top+82)*1.33333), 4210752);
        centered = I18n.format("gui.vendingblock.cost")+" "+buyQnt* vendingTE.getPrice()+ I18n.format("gui.vendingblock.currency");
        this.fontRenderer.drawString(centered, (int)(((this.width/2)*1.33333) - this.fontRenderer.getStringWidth(centered) / 2), (int)((top+92)*1.33333), 4210752);
        GlStateManager.popMatrix();
    }

    private void drawResultString() {
        Color error = new Color(204, 0, 0), success = new Color(0, 187, 0), color;
        String resultString;
        switch (resultType) {
            case NOTOWNER:
                resultString = I18n.format("gui.vendingblock.error.notowner");
                color = error;
                break;
            case NOITEM:
                resultString = I18n.format("gui.vendingblock.error.noitem");
                color = error;
                break;
            case NOMONEY:
                resultString = I18n.format("gui.vendingblock.error.nomoney");
                color = error;
                break;
            case SAMEPLAYER:
                resultString = I18n.format("gui.vendingblock.error.sameplayer");
                color = error;
                break;
            case NOBUYERACCOUNT:
                resultString = I18n.format("gui.vendingblock.error.nobuyeraccount");
                color = error;
                break;
            case NOSELLERACCOUNT:
                resultString = I18n.format("gui.vendingblock.error.noselleraccount");
                color = error;
                break;
            case NOSPACE:
                resultString = I18n.format("gui.vendingblock.error.nospace");
                color = error;
                break;
            case QNTIVALID:
                resultString = I18n.format("gui.vendingblock.error.qntinvalid");
                color = error;
                break;
            case INVALIDUUID:
                resultString = I18n.format("gui.vendingblock.error.invaliduuid");
                color = error;
                break;
            case SUCCESS:
                resultString = I18n.format("gui.vendingblock.success");
                color = success;
                break;
            default:
                resultString = "";
                color = Color.BLACK;
        }
        long updateDelay = System.currentTimeMillis() - lastResultUpdate;
        if(updateDelay < 5000) {
            int resultColor;
            if(updateDelay >= 4500) {
                color = new Color(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F, (5000-updateDelay)/500F);
            }
            resultColor = color.getRGB();
            this.fontRenderer.drawString(resultString, (this.width/2) - this.fontRenderer.getStringWidth(resultString) / 2,top+130, resultColor);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ItemStack stack = vendingTE.getItemStackHandler().getStackType();
        this.drawDefaultBackground();
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(left, top, 0, 0, 200, 145);
        drawButtons(mouseX, mouseY, partialTicks);
        drawStrings();
        //Render item being sold on screen
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.scale(3, 3, 3);
        this.itemRender.renderItemIntoGUI(stack, (left+12)/3,(top+21)/3);
        this.itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, (left+12)/3,(top+21)/3, "");
        RenderHelper.enableStandardItemLighting();
        //End item render on screen
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
        if(!stack.isEmpty() && mouseX >= left+12 && mouseX <= left+12+(16*3) && mouseY >= top+21 && mouseY <= top+21+(16*3)) {
            this.renderToolTip(stack, mouseX, mouseY);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
