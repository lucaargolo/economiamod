package me.d4rk.economiamod.network;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.InsufficientCreditException;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import io.netty.buffer.ByteBuf;
import me.d4rk.economiamod.Utils;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketPurchase implements IMessage {
    private BlockPos tileEntity;
    private int buyQnt;

    @Override
    public void fromBytes(ByteBuf buf) {
        tileEntity = BlockPos.fromLong(buf.readLong());
        buyQnt = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(tileEntity.toLong());
        buf.writeInt(buyQnt);
    }

    public PacketPurchase(VendingBlockTileEntity tileEntity, int buyQnt) {
        this.tileEntity = tileEntity.getPos();
        this.buyQnt = buyQnt;
    }

    //A linha de baixo é completamente inutil, pode deletar /s
    public PacketPurchase() {}

    public static class Handler implements IMessageHandler<PacketPurchase, IMessage> {
        @Override
        public IMessage onMessage(PacketPurchase message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketPurchase message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.tileEntity)) {
                VendingBlockTileEntity serverTE = (VendingBlockTileEntity) world.getTileEntity(message.tileEntity);
                Utils.ResultType resultType;
                try{
                    if(UUID.fromString(serverTE.getOwnerUUID()).equals(playerEntity.getUniqueID())) {
                        resultType = Utils.ResultType.SAMEPLAYER;
                    }else{
                        Long sellerBalance = null;
                        try {
                            sellerBalance = EnderPayApi.getBalance(UUID.fromString(serverTE.getOwnerUUID()));
                        }catch (NoSuchAccountException exception) {}
                        if(sellerBalance != null) {
                            Long buyerBalance = null;
                            try {
                                buyerBalance = EnderPayApi.getBalance(playerEntity.getUniqueID());
                            }catch (NoSuchAccountException exception) { }
                            if(buyerBalance != null) {
                                if (message.buyQnt < 1 && message.buyQnt > serverTE.getItemStackHandler().getItemQuantity()) {
                                    resultType = Utils.ResultType.QNTIVALID;
                                } else if (serverTE.getItemStackHandler().getStackType().isEmpty()) {
                                    resultType = Utils.ResultType.NOITEM;
                                } else if (message.buyQnt * serverTE.getPrice() > buyerBalance) {
                                    resultType = Utils.ResultType.NOMONEY;
                                } else {
                                    //Checar se o inventario do player tem espaço suficiente
                                    int size = 0;
                                    for(int x = 0; x < 36; x++) {
                                        ItemStack current = playerEntity.inventory.getStackInSlot(x);
                                        if(current.isItemEqual(serverTE.getItemStackHandler().getStackType())) {
                                            size += current.getMaxStackSize() - current.getCount();
                                        }else if(current.isEmpty()) {
                                            size += serverTE.getItemStackHandler().getStackType().getMaxStackSize();
                                        }
                                    }
                                    if (size >= message.buyQnt) {
                                        ItemStack stack = serverTE.getItemStackHandler().getStackType();
                                        int left = message.buyQnt;
                                        while (left > 0) {
                                            if(left >= stack.getMaxStackSize()) {
                                                ItemStack copy = stack.copy();
                                                copy.setCount(stack.getMaxStackSize());
                                                playerEntity.inventory.addItemStackToInventory(copy);
                                                left -= stack.getMaxStackSize();
                                            }else{
                                                ItemStack copy = stack.copy();
                                                copy.setCount(left);
                                                playerEntity.inventory.addItemStackToInventory(copy);
                                                left -= stack.getMaxStackSize();
                                            }
                                        }
                                        try {
                                            EnderPayApi.takeFromBalance(playerEntity.getUniqueID(), message.buyQnt * serverTE.getPrice());
                                            EnderPayApi.addToBalance(UUID.fromString(serverTE.getOwnerUUID()), message.buyQnt * serverTE.getPrice());
                                        }catch (InsufficientCreditException | NoSuchAccountException ignored) {}
                                        serverTE.getItemStackHandler().extractQuantity(message.buyQnt);
                                        resultType = Utils.ResultType.SUCCESS;
                                    }else{
                                        resultType = Utils.ResultType.NOSPACE;
                                    }
                                }
                            }else{
                                resultType = Utils.ResultType.NOBUYERACCOUNT;
                            }
                        }else{
                            resultType = Utils.ResultType.NOSELLERACCOUNT;
                        }
                    }
                }catch (IllegalArgumentException exception) {
                    resultType = Utils.ResultType.INVALIDUUID;
                }
                PacketHandler.INSTANCE.sendTo(new PacketPurchaseResponse(resultType), playerEntity);
            }
        }
    }
}
