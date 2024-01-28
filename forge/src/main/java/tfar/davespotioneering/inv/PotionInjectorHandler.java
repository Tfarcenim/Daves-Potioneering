package tfar.davespotioneering.inv;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tfar.davespotioneering.item.CGauntletItem;

import javax.annotation.Nonnull;

import static tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity.BLAZE;
import static tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity.GAUNTLET;

public class PotionInjectorHandler extends BridgedItemStackHandler {

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return switch (slot) {
            case 0, 1, 2, 3, 4, 5 -> stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET -> stack.getItem() instanceof CGauntletItem;
            case BLAZE -> stack.getItem() == Items.BLAZE_POWDER;
            default -> super.isItemValid(slot, stack);
        };
    }
}
