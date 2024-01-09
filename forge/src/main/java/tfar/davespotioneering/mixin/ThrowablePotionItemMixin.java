package tfar.davespotioneering.mixin;

import net.minecraft.world.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrowablePotionItem.class)
public class ThrowablePotionItemMixin {

    @ModifyConstant(method = "use",constant = @Constant(floatValue = .5f))
    private float modifyThrowDistance(float old) {
        return 1f;
    }

}
