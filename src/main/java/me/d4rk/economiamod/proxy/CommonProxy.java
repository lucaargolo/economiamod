package me.d4rk.economiamod.proxy;

import me.d4rk.economiamod.EconomiaMod;
import me.d4rk.economiamod.blocks.BlockCompendium;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlock;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockItemBlock;
import me.d4rk.economiamod.blocks.vendingblock.VendingBlockTileEntity;
import me.d4rk.economiamod.network.PacketHandler;
import me.d4rk.economiamod.waila.WailaCompatibility;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;


import static me.d4rk.economiamod.EconomiaMod.instance;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.registerMessages("economiamod");
        if (Loader.isModLoaded("waila")) {
            WailaCompatibility.register();
        }
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiProxy());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new VendingBlock());
        GameRegistry.registerTileEntity(VendingBlockTileEntity.class, EconomiaMod.MODID + "_vendingblock");

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new VendingBlockItemBlock(BlockCompendium.vendingBlock).setRegistryName(BlockCompendium.vendingBlock.getRegistryName()));
    }

}