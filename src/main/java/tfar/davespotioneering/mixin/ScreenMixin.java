package tfar.davespotioneering.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.ClientEvents;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow public abstract List<Text> getTooltipFromItem(ItemStack stack);

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",at = @At("HEAD"),cancellable = true)
    private void wrapTooltips(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
        ClientEvents.renderWrappedToolTip((Screen) (Object)this,stack,matrices,this.getTooltipFromItem(stack),x,y, MinecraftClient.getInstance().textRenderer);
        ci.cancel();
    }
}
