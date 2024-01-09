package tfar.davespotioneering.inv.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FuelSlot extends SlotItemHandler {
    public FuelSlot(IItemHandler iInventoryIn, int index, int xPosition, int yPosition) {
        super(iInventoryIn, index, xPosition, yPosition);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack stack) {
        return isValidBrewingFuel(stack);
    }

    /**
     * Returns true if the given ItemStack is usable as a fuel in the brewing stand.
     */
    public static boolean isValidBrewingFuel(ItemStack itemStackIn) {
        return itemStackIn.getItem() == Items.BLAZE_POWDER;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
     * case of armor slots)
     */
    public int getMaxStackSize() {
        return 64;
    }
}
