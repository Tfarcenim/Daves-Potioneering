package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tfar.davespotioneering.item.UmbrellaItem;

@Mixin(Player.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @ModifyArg(method = "disableShield",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"))
    private int moreDelay(int old) {
        return this.getUseItem().getItem() instanceof UmbrellaItem ? 200 : old;
    }
}
