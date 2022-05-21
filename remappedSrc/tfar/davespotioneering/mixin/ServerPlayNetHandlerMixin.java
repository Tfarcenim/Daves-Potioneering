package tfar.davespotioneering.mixin;

import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Events;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleSetCarriedItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"))
    private void heldItemChange(UpdateSelectedSlotC2SPacket itemChangePacket, CallbackInfo ci) {
        Events.heldItemChangeEvent(this.player);
    }
}
