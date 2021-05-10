package tfar.davespotioneering.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.ModConfig;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "resetActiveHand",at = @At("RETURN"))
    private void potionCooldown(CallbackInfo ci) {
        if ((Object)this instanceof PlayerEntity && !world.isRemote) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            player.getCooldownTracker().setCooldown(Items.SPLASH_POTION, ModConfig.Server.potion_cooldown);
            player.getCooldownTracker().setCooldown(Items.LINGERING_POTION, ModConfig.Server.potion_cooldown);
        }
    }
}
