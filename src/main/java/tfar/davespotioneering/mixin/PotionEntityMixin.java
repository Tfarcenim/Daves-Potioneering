package tfar.davespotioneering.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.Util;

import java.util.Iterator;
import java.util.List;

@Mixin(PotionEntity.class)
abstract class PotionEntityMixin extends ProjectileItemEntity {

    public PotionEntityMixin(EntityType<? extends ProjectileItemEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "func_213888_a", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"),locals = LocalCapture.CAPTURE_FAILHARD)
    private void milkify(List<EffectInstance> arg0, Entity directHit, CallbackInfo ci, AxisAlignedBB axisalignedbb, List<LivingEntity> list,
                         Iterator<LivingEntity> var5, LivingEntity livingentity, double d0) {
        if (Util.isMilkified(getItem())) {
            livingentity.clearActivePotions();
        }
    }
}
