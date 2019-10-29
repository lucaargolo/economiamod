package me.d4rk.economiamod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int packetId = 0;

    public static SimpleNetworkWrapper INSTANCE = null;

    public PacketHandler() {
    }

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(PacketUpdatePrice.Handler.class, PacketUpdatePrice.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketOpenContainer.Handler.class, PacketOpenContainer.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketPurchase.Handler.class, PacketPurchase.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketPurchaseResponse.Handler.class, PacketPurchaseResponse.class, nextID(), Side.CLIENT);
    }
}
