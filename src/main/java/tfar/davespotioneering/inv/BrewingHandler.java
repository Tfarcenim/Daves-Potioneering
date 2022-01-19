package tfar.davespotioneering.inv;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;

public class BrewingHandler extends ItemStackHandler {

    public BrewingHandler(int size) {
        super(size);
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot < AdvancedBrewingStandBlockEntity.POTIONS.length ? 2 : super.getSlotLimit(slot);
    }
}
