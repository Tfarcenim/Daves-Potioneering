package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.Util;

import java.util.Iterator;
import java.util.List;

@Mixin(ThrownPotion.class)
abstract class PotionEntityMixin extends ThrowableItemProjectile {

    public PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "applySplash", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void milkify(List<MobEffectInstance> arg0, Entity directHit, CallbackInfo ci, AABB axisalignedbb, List<LivingEntity> list,
                         Iterator<LivingEntity> var5, LivingEntity livingentity, double d0) {
        if (Util.isMilkified(getItem())) {
            livingentity.removeAllEffects();
        }
    }
}
