package tfar.davespotioneering.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.DavesPotioneeeringClientFabric;

@Mixin(MouseHandler.class)
public class MouseHelperMixin {

    //replacement for Forge's InputEvent.MouseInputEvent
    @Inject(method = "onPress",at = @At("RETURN"))
    private void inputEventMouseInputEventHook(int button, CallbackInfo ci) {
        DavesPotioneeeringClientFabric.onMouseInput(button);
    }

    //replacement for Forge's InputEvent.MouseScrollEvent
    @Inject(method = "onScroll",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),cancellable = true)
    private void inputEventMouseScrollEvent(long l, double d, double e, CallbackInfo ci) {
        if (DavesPotioneeeringClientFabric.onMouseScroll(0)) {//todo needs a local
            ci.cancel();
        }
    }
}
