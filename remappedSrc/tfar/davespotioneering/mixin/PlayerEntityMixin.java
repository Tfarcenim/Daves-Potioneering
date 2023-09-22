package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.item.UmbrellaItem;

@Mixin(Player.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @ModifyArg(method = "disableShield",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
    private int moreDelay(int old) {
        return this.getUseItem().getItem() instanceof UmbrellaItem ? 200 : old;
    }

    @Inject(method = "eatFood",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/ConsumeItemCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void onFoodEat(Level p_213357_1_, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Events.onEat((Player) (Object)this,stack);
    }
}
