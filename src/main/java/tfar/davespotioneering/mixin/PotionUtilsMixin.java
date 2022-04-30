package tfar.davespotioneering.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.init.ModPotions;

import java.util.Collection;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {

    @Inject(method = "getPotionColorFromEffectList",at = @At("RETURN"),cancellable = true)
    private static void modifyColor(Collection<MobEffectInstance> instances, CallbackInfoReturnable<Integer> cir) {
        int old = cir.getReturnValue();
        if (old == 0) {
            for(MobEffectInstance effectinstance : instances) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    int k = effectinstance.getEffect().getColor();
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
