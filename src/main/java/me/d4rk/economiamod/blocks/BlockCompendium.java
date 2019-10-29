package me.d4rk.economiamod.blocks;

import me.d4rk.economiamod.blocks.vendingblock.VendingBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCompendium {

    @GameRegistry.ObjectHolder("economiamod:vendingblock")
    public static VendingBlock vendingBlock;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        vendingBlock.initModel();
    }

}

