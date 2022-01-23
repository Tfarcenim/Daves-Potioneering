package tfar.davespotioneering.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Events;

@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "processHeldItemChange",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/player/ServerPlayerEntity;markPlayerActive()V"))
    private void heldItemChange(CHeldItemChangePacket itemChangePacket, CallbackInfo ci) {
        Events.heldItemChangeEvent(this.player);
    }
}
