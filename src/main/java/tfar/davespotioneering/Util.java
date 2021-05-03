package tfar.davespotioneering;

import net.minecraft.item.Item;
import tfar.davespotioneering.mixin.ItemAccess;

public class Util {

    public static void setStackSize(Item item, int count) {
        ((ItemAccess)item).setMaxStackSize(count);
    }
}
