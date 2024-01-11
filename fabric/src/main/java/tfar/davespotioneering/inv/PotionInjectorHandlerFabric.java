package tfar.davespotioneering.inv;

import tfar.davespotioneering.item.GauntletItemFabric;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PotionInjectorHandlerFabric extends BridgedSimpleContainer {

    public static final int GAUNTLET = 6;
    public static final int BLAZE = 7;

    public PotionInjectorHandlerFabric(int slots) {
        super(slots);
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        switch (slot) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return stack.getItem() == Items.LINGERING_POTION;
            case GAUNTLET:
                return stack.getItem() instanceof GauntletItemFabric;
            case BLAZE:
                return stack.getItem() == Items.BLAZE_POWDER;
        }
        return super.canPlaceItem(slot, stack);
    }
}
