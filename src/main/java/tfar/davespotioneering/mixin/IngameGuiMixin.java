package tfar.davespotioneering.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.item.GauntletItem;

@Mixin(InGameHud.class)
public class IngameGuiMixin {
    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void cancelItemNameForGauntlet(MatrixStack matrixStack, CallbackInfo ci) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = minecraft.player;
        if (player != null) {
            if (GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR &&
                    this.client.player.getMainHandStack().getItem() instanceof GauntletItem) {
                ci.cancel();
            }
        }
    }
}
