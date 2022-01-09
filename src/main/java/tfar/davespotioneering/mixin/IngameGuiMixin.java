package tfar.davespotioneering.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.item.GauntletItem;

@Mixin(IngameGui.class)
public class IngameGuiMixin {
    @Shadow @Final protected Minecraft mc;

    @Inject(method = "renderItemName", at = @At("HEAD"), cancellable = true)
    private void cancelItemNameForGauntlet(MatrixStack matrixStack, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;
        if (player != null) {
            if (GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR &&
                    this.mc.player.getHeldItemMainhand().getItem() instanceof GauntletItem) {
                ci.cancel();
            }
        }
    }
}
