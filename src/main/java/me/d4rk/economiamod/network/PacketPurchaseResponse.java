package me.d4rk.economiamod.network;

import io.netty.buffer.ByteBuf;
import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.Utils;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import me.d4rk.economiamod.gui.VendingBlockNormalGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.UUID;

public class PacketPurchaseResponse implements IMessage {
    Utils.ResultType result;

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        StringBuilder string = new StringBuilder();
        for(int x = 0; x < length; x++) {
            string.append(buf.readChar());
        }
        result = Utils.ResultType.valueOf(string.toString());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(result.toString().length());
        for(int x = 0; x < result.toString().length(); x++) {
            buf.writeChar(result.toString().charAt(x));
        }
    }

    public PacketPurchaseResponse(Utils.ResultType result) {
        this.result = result;
    }

    //A linha de baixo é completamente inutil, pode deletar /s
    public PacketPurchaseResponse() {}

    public static class Handler implements IMessageHandler<PacketPurchaseResponse, IMessage> {
        @Override
        public IMessage onMessage(PacketPurchaseResponse message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketPurchaseResponse message, MessageContext ctx) {
            if(Minecraft.getMinecraft().currentScreen != null && (Minecraft.getMinecraft().currentScreen instanceof VendingBlockNormalGui)) {
                ((VendingBlockNormalGui) Minecraft.getMinecraft().currentScreen).receiveResponse(message.result);
            }
        }
    }
}
