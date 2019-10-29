package me.d4rk.economiamod.blocks.vendingblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class VendingBlockTileEntity extends TileEntity {

    public static final int SIZE = 27;

    private String ownerUUID, ownerName;
    private int price;

    private VendingBlockItemStackHandler itemStackHandler = new VendingBlockItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            VendingBlockTileEntity.this.markDirty();
        }
    };

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readFromNBT(packet.getNbtCompound());
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("items")) itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        if (compound.hasKey("owner")) this.ownerUUID = compound.getString("owner");
        if (compound.hasKey("price")) this.price = compound.getInteger("price");
        if (compound.hasKey("name")) this.ownerName = compound.getString("name");
        if(this.price < 1) this.price = 1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        if(ownerUUID == null) ownerUUID = "";
        compound.setString("owner", ownerUUID);
        if(ownerName == null) ownerName = "";
        compound.setString("name", ownerName);
        compound.setInteger("price", price);
        return compound;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public VendingBlockItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setPrice(int price) {
        this.price = price;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    public int getPrice() {
        return price;
    }

    public void deleteItems() {
        this.itemStackHandler = new VendingBlockItemStackHandler(SIZE);
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }
}