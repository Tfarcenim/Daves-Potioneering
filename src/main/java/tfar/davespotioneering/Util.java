package tfar.davespotioneering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tfar.davespotioneering.mixin.ItemAccess;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess)item).setMaxStackSize(count);
    }

    public static final String MILKIFY = "milkified";

    public static void milkifyPotion(ItemStack potion) {
        potion.getOrCreateTag().putBoolean(MILKIFY,true);
    }

    public static boolean isMilkified(ItemStack potion) {
        return potion.hasTag() && potion.getTag().getBoolean(MILKIFY);
    }
}
