package tfar.davespotioneering.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.init.ModPotions;

import java.util.Collection;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.PotionUtil;

@Mixin(PotionUtil.class)
public class PotionUtilsMixin {

    @Inject(method = "getColor(Ljava/util/Collection;)I",at = @At("RETURN"),cancellable = true)
    private static void modifyColor(Collection<StatusEffectInstance> instances, CallbackInfoReturnable<Integer> cir) {
        int old = cir.getReturnValue();
        if (old == 0) {
            for(StatusEffectInstance effectinstance : instances) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    int k = effectinstance.getEffectType().getColor();
                    int l = 1;
                    float r = (float)(l * (k >> 16 & 255)) / 255.0F;
                    float g = (float)(l * (k >> 8 & 255)) / 255.0F;
                    float b = (float)(l * (k & 255)) / 255.0F;

                    r = r * 255.0F;
                    g = g * 255.0F;
                    b = b * 255.0F;
                    cir.setReturnValue((int)r << 16 | (int)g << 8 | (int)b);
                }
            }
        }
    }
}
