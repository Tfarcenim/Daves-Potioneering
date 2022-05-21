package tfar.davespotioneering.inv;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class SidedItemHandler implements Inventory {
    protected final BrewingHandler inv;
    private final Direction direction;


    public SidedItemHandler(BrewingHandler inv,Direction direction) {
        this.inv = inv;
        this.direction = direction;
    }

    public int mapSlot(int slot) {
        int[] accessible = inv.getSlotsForFace(direction);
        if (slot < accessible.length)
            return accessible[slot];
        return -1;
    }

    @Override
    public int size() {
        return inv.getSlotsForFace(direction).length;
    }

    @Override
    @Nonnull
    public ItemStack getStack(int slot) {
        int i = mapSlot(slot);
        return i == -1 ? ItemStack.EMPTY : inv.getStack(i);
    }

    @Override
    public ItemStack removeStack(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);

        if (slot1 != -1)
            inv.setStack(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack removeStack(int slot, int amount) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = mapSlot(slot);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        return inv.removeStack(slot1,amount);
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);
        return slot1 != -1 && inv.isItemValid(slot1, stack);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }
}