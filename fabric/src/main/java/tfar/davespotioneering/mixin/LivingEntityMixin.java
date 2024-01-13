package tfar.davespotioneering.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.FabricEvents;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "canBeAffected",at = @At("HEAD"),cancellable = true)
    private void isApplicable(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        if (!DavesPotioneering.canApplyEffect((LivingEntity)(Object)this)) {
            cir.setReturnValue(false);
        }
    }
}
