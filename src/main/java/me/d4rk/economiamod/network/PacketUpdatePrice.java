package me.d4rk.economiamod.network;

import io.netty.buffer.ByteBuf;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.collection.parallel.ParIterableLike;

import java.util.UUID;

public class PacketUpdatePrice implements IMessage {
    private BlockPos tileEntity;
    private int price;

    @Override
    public void fromBytes(ByteBuf buf) {
        price = buf.readInt();
        tileEntity = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(price);
        buf.writeLong(tileEntity.toLong());
    }

    public PacketUpdatePrice(VendingBlockTileEntity tileEntity, int price) {
        this.tileEntity = tileEntity.getPos();
        this.price = price;
    }

    //A linha de baixo é completamente inutil, pode deletar /s
    public PacketUpdatePrice() {}

    public static class Handler implements IMessageHandler<PacketUpdatePrice, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdatePrice message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketUpdatePrice message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();

            if (world.isBlockLoaded(message.tileEntity)) {
                VendingBlockTileEntity serverTE = (VendingBlockTileEntity) world.getTileEntity(message.tileEntity);
                if(playerEntity.getUniqueID().equals(UUID.fromString(serverTE.getOwnerUUID()))) serverTE.setPrice(message.price);
            }
        }
    }
}
