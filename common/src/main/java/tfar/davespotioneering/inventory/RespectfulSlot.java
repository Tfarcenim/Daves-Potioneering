package tfar.davespotioneering.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RespectfulSlot extends Slot {

    protected final int slotIndex;
    public RespectfulSlot(Container $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
        slotIndex = $$1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return container.canPlaceItem(slotIndex, stack);
    }
}
