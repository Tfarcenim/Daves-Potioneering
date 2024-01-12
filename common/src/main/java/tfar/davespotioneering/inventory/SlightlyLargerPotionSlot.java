package tfar.davespotioneering.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.BrewingStandMenu;

public class SlightlyLargerPotionSlot extends BrewingStandMenu.PotionSlot {
    public SlightlyLargerPotionSlot(Container $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Override
    public int getMaxStackSize() {
        return 2;
    }
}
