package tfar.davespotioneering.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.item.GauntletItem;

@Mixin(Gui.class)
public class IngameGuiMixin {
    @Shadow @Final
    private Minecraft minecraft;

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void cancelItemNameForGauntlet(PoseStack matrixStack, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null) {
            if (GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR &&
                    this.minecraft.player.getMainHandItem().getItem() instanceof GauntletItem) {
                ci.cancel();
            }
        }
    }
}
