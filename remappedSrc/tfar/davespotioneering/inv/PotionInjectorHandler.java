package tfar.davespotioneering.inv;

import tfar.davespotioneering.item.GauntletItem;

import javax.annotation.Nonnull;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PotionInjectorHandler extends SimpleInventory {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemStack stack) {
        switch (slot) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET:
                return stack.getItem() instanceof GauntletItem;
            case BLAZE:
                return stack.getItem() == Items.BLAZE_POWDER;
        }
        return super.isValid(slot, stack);
    }
}
