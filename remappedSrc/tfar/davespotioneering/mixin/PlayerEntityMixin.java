package tfar.davespotioneering.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tfar.davespotioneering.item.UmbrellaItem;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @ModifyArg(method = "disableShield",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"))
    private int moreDelay(int old) {
        return this.getActiveItem().getItem() instanceof UmbrellaItem ? 200 : old;
    }
}