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
    @Inject(method = "onTake",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancements/critereon/BrewedPotionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/alchemy/Potion;)V"))
    private void onBrew(PlayerEntity player, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        Events.playerTakedBrewedPotion(player);
    }
}
