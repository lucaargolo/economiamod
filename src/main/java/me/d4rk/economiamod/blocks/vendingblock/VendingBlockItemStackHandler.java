package me.d4rk.economiamod.blocks.vendingblock;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class VendingBlockItemStackHandler extends ItemStackHandler {

    public VendingBlockItemStackHandler(int size) {
        super(size);
    }

    public int getItemQuantity() {
        int size = 0;
        for(int x = 0; x < getSlots(); x++) {
            ItemStack stack = getStackInSlot(x);
            if(!stack.isEmpty() && stack.isItemEqual(getStackType())) size += stack.getCount();
        }
        return size;
    }

    public ItemStack getStackType() {
        ItemStack stack = ItemStack.EMPTY;
        for(int x = 0; x < getSlots(); x++) {
            stack = getStackInSlot(x);
            if(!stack.isEmpty()) break;
        }
        return stack;
    }

    public void extractQuantity(int qnt) {
        int slotIndex = 0;
        while(qnt > 0) {
            if(extractItem(slotIndex, 1, true).isEmpty()) slotIndex++;
            else{
                extractItem(slotIndex, 1, false);
                qnt--;
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(!getStackType().isEmpty() && !getStackType().isItemEqual(stack)) return stack;
        if((!getStackType().isEmpty()) && (getStackType().hasTagCompound() != stack.hasTagCompound())) return stack;
        if((!getStackType().isEmpty()) && (getStackType().hasTagCompound() == stack.hasTagCompound()) && getStackType().hasTagCompound() && !getStackType().getTagCompound().equals(stack.getTagCompound())) return stack;
        return super.insertItem(slot, stack, simulate);
    }
}
