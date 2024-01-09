package tfar.davespotioneering.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.FabricEvents;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "useItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    private void hook(ServerPlayer serverPlayerEntity, Level world, ItemStack itemStack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        FabricEvents.potionCooldown(serverPlayerEntity,world,hand);
    }
}
