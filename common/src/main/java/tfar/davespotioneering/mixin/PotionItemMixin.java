package tfar.davespotioneering.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    /**
     * @author Tfar
     * @param stack
     * @return
     */
    @Overwrite
    public int getUseDuration(ItemStack stack) {
        return 20;//half of 32
    }
}
