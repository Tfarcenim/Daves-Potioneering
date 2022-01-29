package tfar.davespotioneering.inv;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class SidedItemHandler implements IItemHandlerModifiable {
    protected final BrewingHandler inv;
    private final Direction direction;


    public static Map<Direction,LazyOptional<? extends IItemHandler>> create(BrewingHandler inv) {
        Map<Direction,LazyOptional<? extends IItemHandler>> ret = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            IItemHandlerModifiable iItemHandler = new SidedItemHandler(inv,direction);
            ret.put(direction,LazyOptional.of(() -> iItemHandler));
        }
        return ret;
    }

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
    public int getSlots() {
        return inv.getSlotsForFace(direction).length;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        int i = mapSlot(slot);
        return i == -1 ? ItemStack.EMPTY : inv.getStackInSlot(i);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        int slot1 = mapSlot(slot);

        if (slot1 == -1)
            return stack;

        return inv.insertItem(slot1,stack,simulate);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);

        if (slot1 != -1)
            inv.setStackInSlot(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = mapSlot(slot);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        return inv.extractItem(slot1,amount,simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv.getSlotLimit(mapSlot(slot));
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        int slot1 = mapSlot(slot);
        return slot1 != -1 && inv.isItemValid(slot1, stack);
    }
}