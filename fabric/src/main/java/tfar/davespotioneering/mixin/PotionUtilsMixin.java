package tfar.davespotioneering.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.DavesPotioneeringClient;
import tfar.davespotioneering.init.ModPotions;

import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;

@Mixin(PotionUtils.class)
public class PotionUtilsMixin {

    @Inject(method = "getColor(Ljava/util/Collection;)I",at = @At("RETURN"),cancellable = true)
    private static void modifyColor(Collection<MobEffectInstance> instances, CallbackInfoReturnable<Integer> cir) {
        int old = cir.getReturnValue();
        if (old == 0) {
            for(MobEffectInstance effectinstance : instances) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    cir.setReturnValue(DavesPotioneeringClient.computeinvis2Color(effectinstance));
                }
            }
        }
    }
}
