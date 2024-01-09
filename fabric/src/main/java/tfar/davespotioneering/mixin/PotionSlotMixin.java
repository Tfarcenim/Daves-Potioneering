package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.FabricEvents;

@Mixin(BrewingStandMenu.PotionSlot.class)
public class PotionSlotMixin {
    @Inject(method = "onTake",at = @At(value = "INVOKE",target = "Lnet/minecraft/advancements/critereon/BrewedPotionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/alchemy/Potion;)V"))
    private void onBrew(Player playerEntity, ItemStack itemStack, CallbackInfo ci) {
        FabricEvents.playerTakedBrewedPotion(playerEntity);
    }
}
