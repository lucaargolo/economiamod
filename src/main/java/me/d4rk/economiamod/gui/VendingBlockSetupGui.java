package me.d4rk.economiamod.gui;

import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import me.d4rk.economiamod.network.PacketHandler;
import me.d4rk.economiamod.network.PacketUpdatePrice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class VendingBlockSetupGui extends GuiScreen {

    private static final int[] NUMBER_KEYS = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82 };

    public static boolean isKeyNumber (int pressedKey) {
        for (int key : NUMBER_KEYS) {
            if(key == pressedKey) return true;
        }
        return false;
    }

    private GuiTextField price;
    private GuiButton buttonDone;

    private final VendingBlockTileEntity vendingTE;

    public VendingBlockSetupGui(VendingBlockTileEntity vendingBlockTileEntity) {
        this.vendingTE = vendingBlockTileEntity;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        int left = ((this.width - 100) / 2), top = ((this.height - 60) / 2);
        this.price = new GuiTextField(0, fontRenderer, left+40, top+20, 60, 10);
        this.buttonDone = this.addButton(new GuiButton(0, left, top+40, 98, 20, "Done"));
    }

    @Override
    public void updateScreen() {
        this.price.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonDone && button.enabled) {
            boolean numberFormatError = false;
            try{
                int price = Integer.parseInt(this.price.getText());
                if(price <= 0 || price > Integer.MAX_VALUE/2000) numberFormatError = true;
                else {
                    vendingTE.setPrice(price);
                    PacketHandler.INSTANCE.sendToServer(new PacketUpdatePrice(vendingTE, price));
                }
            }catch(NumberFormatException exception){
                numberFormatError = true;
            }
            if(numberFormatError) {
                TextComponentString string = new TextComponentString(I18n.format("gui.vendingblock.invalidprice"));
                string.getStyle().setColor(TextFormatting.RED);
                mc.player.sendMessage(string);
            }
            mc.displayGuiScreen(null);
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) {

        if (isKeyNumber(keyCode) || keyCode == 14) {
            if(this.price.isFocused()) this.price.textboxKeyTyped(typedChar, keyCode);
            return;
        }

        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        } else if (keyCode == 28) {
            this.actionPerformed(this.buttonDone);
        }

        if (this.price.isFocused()) {
            switch (keyCode) {
                case 203:
                    this.price.setCursorPosition(this.price.getCursorPosition() - 1);
                    break;
                case 205:
                    this.price.setCursorPosition(this.price.getCursorPosition() + 1);
                    break;
                case 200:
                    this.price.setCursorPosition(0);
                    break;
                case 208:
                    this.price.setCursorPosition(this.price.getText().length());
                    break;
            }
        }
    }

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.price.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen ( int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        int left = (this.width / 2), top = ((this.height - 60) / 2);
        drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format("gui.vendingblock.setup")+":", left, top, 10526880);
        drawString(Minecraft.getMinecraft().fontRenderer, I18n.format("gui.vendingblock.price")+":", left-50, top+20, 10526880);
        price.drawTextBox();
        buttonDone.drawButton(mc, mouseX, mouseY, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}