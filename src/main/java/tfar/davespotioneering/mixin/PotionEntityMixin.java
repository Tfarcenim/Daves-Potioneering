package tfar.davespotioneering.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Util;

import java.util.List;

@Mixin(ThrownPotion.class)
abstract class PotionEntityMixin extends ThrowableItemProjectile {

    public PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "applySplash", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    private void milkify(List<MobEffectInstance> p_37548_, Entity p_37549_, CallbackInfo ci) {
        if (Util.isMilkified(getItem()) && p_37549_ instanceof LivingEntity livingEntity) {
            livingEntity.removeAllEffects();
        }
    }
}
