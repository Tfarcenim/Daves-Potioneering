package tfar.davespotioneering.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.ClientEvents;

@Mixin(MouseHandler.class)
public class MouseHelperMixin {

    //replacement for Forge's InputEvent.MouseInputEvent
    @Inject(method = "onMouseButton",at = @At("RETURN"))
    private void inputEventMouseInputEventHook(long handle, int button, int action, int mods, CallbackInfo ci) {
        ClientEvents.onMouseInput(handle, button, action, mods);
    }

    //replacement for Forge's InputEvent.MouseScrollEvent
    @Inject(method = "onMouseScroll",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"),cancellable = true)
    private void inputEventMouseScrollEvent(long l, double d, double e, CallbackInfo ci) {
        if (ClientEvents.onMouseScroll(0)) {//todo needs a local
            ci.cancel();
        }
    }
}
