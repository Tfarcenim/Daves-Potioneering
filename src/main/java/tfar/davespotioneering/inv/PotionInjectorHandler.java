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
        return super.isItemValid(slot, stack);
    }
}
