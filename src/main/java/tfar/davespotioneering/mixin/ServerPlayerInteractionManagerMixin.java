package tfar.davespotioneering.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.Events;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "interactItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private void hook(ServerPlayerEntity serverPlayerEntity, World world, ItemStack itemStack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Events.potionCooldown(serverPlayerEntity,world,hand);
    }
}
