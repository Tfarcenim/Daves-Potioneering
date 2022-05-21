package tfar.davespotioneering.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.Events;

@Mixin(BrewingStandScreenHandler.PotionSlot.class)
public class PotionSlotMixin {
    @Inject(method = "onTakeItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/BrewedPotionCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/potion/Potion;)V"))
    private void onBrew(PlayerEntity player, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        Events.playerTakedBrewedPotion(player);
    }
}
