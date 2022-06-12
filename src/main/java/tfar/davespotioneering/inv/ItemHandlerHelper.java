package tfar.davespotioneering.inv;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
    public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b)
    {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasNbt() != b.hasNbt())
            return false;

        return (!a.hasNbt() || a.getNbt().equals(b.getNbt()));
    }

    @Nonnull
    public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size)
    {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }

}
