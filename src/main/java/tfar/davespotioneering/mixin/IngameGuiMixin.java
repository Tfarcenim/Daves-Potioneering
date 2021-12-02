package tfar.davespotioneering.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.GauntletHUD;

@Mixin(IngameGui.class)
public class IngameGuiMixin {
    @Inject(method = "renderItemName", at = @At("HEAD"), cancellable = true)
    private void cancelItemNameForGauntlet(MatrixStack matrixStack, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if (player != null) {
            if (GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR) {
                ci.cancel();
            }
        }
    }
}
