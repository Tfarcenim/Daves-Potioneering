package tfar.davespotioneering.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface BasicInventoryBridge {

    ItemStack $getStackInSlot(int slot);
    void $setStackInSlot(int slot,ItemStack stack);

    NonNullList<ItemStack> $getStacks();

}