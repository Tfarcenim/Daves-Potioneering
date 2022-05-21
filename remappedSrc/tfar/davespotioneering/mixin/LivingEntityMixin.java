package tfar.davespotioneering.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.Events;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "canBeAffected",at = @At("HEAD"),cancellable = true)
    private void isApplicable(StatusEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        if (!Events.canApplyEffect((LivingEntity)(Object)this)) {
            cir.setReturnValue(false);
        }
    }
}
