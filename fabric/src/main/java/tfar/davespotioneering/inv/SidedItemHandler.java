package tfar.davespotioneering.inv;

import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SidedItemHandler implements Container {
    protected final BrewingHandlerFabric inv;
    private final Direction direction;


    public SidedItemHandler(BrewingHandlerFabric inv, Direction direction) {
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
    public int getContainerSize() {
        return inv.getSlotsForFace(direction).length;
    }

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        int i = mapSlot(slot);
        return i == -1 ? ItemStack.EMPTY : inv.getItem(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);

        if (slot1 != -1)
            inv.setItem(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = mapSlot(slot);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        return inv.removeItem(slot1,amount);
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);
        return slot1 != -1 && inv.isItemValid(slot1, stack);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {

    }
}