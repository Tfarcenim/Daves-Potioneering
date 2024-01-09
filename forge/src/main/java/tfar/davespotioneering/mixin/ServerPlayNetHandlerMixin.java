package tfar.davespotioneering.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.ForgeEvents;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetHandlerMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "handleSetCarriedItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"))
    private void heldItemChange(ServerboundSetCarriedItemPacket itemChangePacket, CallbackInfo ci) {
        ForgeEvents.heldItemChangeEvent(this.player);
    }
}
