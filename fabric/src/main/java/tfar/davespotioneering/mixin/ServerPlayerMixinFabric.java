package tfar.davespotioneering.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.item.CGauntletItem;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixinFabric {

    @Inject(method = "tick",at = @At("HEAD"))
    private void tickPlayer(CallbackInfo ci) {
        CGauntletItem.tickCooldownsCommon((ServerPlayer)(Object) this);
    }
}
