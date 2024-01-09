package tfar.davespotioneering.inv;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import tfar.davespotioneering.item.GauntletItem;

import javax.annotation.Nonnull;

public class PotionInjectorHandler extends ItemStackHandler {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public PotionInjectorHandler(int slots) {
        super(slots);
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return switch (slot) {
            case 0, 1, 2, 3, 4, 5 -> stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET -> stack.getItem() instanceof GauntletItem;
            case BLAZE -> stack.getItem() == Items.BLAZE_POWDER;
            default -> super.isItemValid(slot, stack);
        };
    }
}
