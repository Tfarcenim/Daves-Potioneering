package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Events;

@Mixin(BrewingStandMenu.PotionSlot.class)
public class PotionSlotMixin {
    @Inject(method = "onTakeItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancement/criterion/BrewedPotionCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/potion/Potion;)V"))
    private void onBrew(Player playerEntity, ItemStack itemStack, CallbackInfo ci) {
        Events.playerTakedBrewedPotion(playerEntity);
    }
}
