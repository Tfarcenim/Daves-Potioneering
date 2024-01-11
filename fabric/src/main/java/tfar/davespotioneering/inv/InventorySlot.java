package tfar.davespotioneering.inv;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InventorySlot extends Slot {
    private final PotionInjectorHandlerFabric inventory;

    public InventorySlot(PotionInjectorHandlerFabric inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return inventory.canPlaceItem(index,stack);
    }
}
