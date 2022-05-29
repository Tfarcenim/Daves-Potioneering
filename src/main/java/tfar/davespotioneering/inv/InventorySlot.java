package tfar.davespotioneering.inv;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class InventorySlot extends Slot {
    private final PotionInjectorHandler inventory;

    public InventorySlot(PotionInjectorHandler inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return inventory.isValid(id,stack);
    }
}
