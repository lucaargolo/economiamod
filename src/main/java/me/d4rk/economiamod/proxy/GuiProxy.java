package me.d4rk.economiamod.proxy;

import me.d4rk.economiamod.blocks.vendingblock.*;
import me.d4rk.economiamod.gui.VendingBlockNormalGui;
import me.d4rk.economiamod.gui.VendingBlockSetupGui;
import me.d4rk.economiamod.gui.VendingBlockStorageGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof VendingBlockTileEntity)) return null;
        VendingBlockTileEntity vte = (VendingBlockTileEntity) te;
        switch (ID) {
            case 1:
                return new VendingBlockStorageContainer(player.inventory, vte);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof VendingBlockTileEntity)) return null;
        VendingBlockTileEntity vte = (VendingBlockTileEntity) te;
        switch (ID) {
            case 0:
                return new VendingBlockNormalGui(vte);
            case 1:
                return new VendingBlockStorageGui(vte, new VendingBlockStorageContainer(player.inventory, vte));
            case 2:
                return new VendingBlockSetupGui(vte);
            default:
                return null;
        }
    }
}