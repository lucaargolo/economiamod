package me.d4rk.economiamod.network;

import io.netty.buffer.ByteBuf;
import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;


public class PacketOpenContainer implements IMessage {
    private BlockPos tileEntity;

    @Override
    public void fromBytes(ByteBuf buf) {
        tileEntity = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(tileEntity.toLong());
    }

    public PacketOpenContainer(VendingBlockTileEntity tileEntity) {
        this.tileEntity = tileEntity.getPos();
    }

    //A linha de baixo é completamente inutil, pode deletar /s
    public PacketOpenContainer() {}

    public static class Handler implements IMessageHandler<PacketOpenContainer, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenContainer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketOpenContainer message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.tileEntity)) {
                VendingBlockTileEntity serverTE = (VendingBlockTileEntity) world.getTileEntity(message.tileEntity);
                if(playerEntity.getUniqueID().equals(UUID.fromString(serverTE.getOwnerUUID())))
                    playerEntity.openGui(EconomiaMod.instance, 1, world, message.tileEntity.getX(), message.tileEntity.getY(), message.tileEntity.getZ());
            }
        }
    }
}
