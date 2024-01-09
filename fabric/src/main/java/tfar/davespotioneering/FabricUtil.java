package tfar.davespotioneering;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class FabricUtil {
    public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] potionIndexes) {
        for (int i : potionIndexes) {
            ItemStack potion = inputs.get(i);
            ItemStack output = PotionBrewing.mix(ingredient, potion);
            output.setCount(inputs.get(i).getCount());
            if (!output.isEmpty()) {
                inputs.set(i, output);
            }
        }
    }

    public static boolean isValidInputCountInsensitive(ItemStack stack) {
       return BrewingStandMenu.PotionSlot.mayPlaceItem(stack);
    }
}
