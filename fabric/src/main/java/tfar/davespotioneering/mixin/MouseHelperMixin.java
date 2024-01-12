package tfar.davespotioneering.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.client.DavesPotioneeeringClientFabric;

@Mixin(MouseHandler.class)
public class MouseHelperMixin {

    //replacement for Forge's InputEvent.MouseInputEvent
    @Inject(method = "onPress",at = @At("RETURN"))
    private void inputEventMouseInputEventHook(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        DavesPotioneeeringClientFabric.onMouseInput(button);
    }

    //replacement for Forge's InputEvent.MouseScrollEvent
    @Inject(method = "onScroll",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z")
            ,cancellable = true,locals = LocalCapture.CAPTURE_FAILHARD)
    private void inputEventMouseScrollEvent(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, double d) {
        if (DavesPotioneeeringClientFabric.onMouseScroll(d)) {
            ci.cancel();
        }
    }
}
