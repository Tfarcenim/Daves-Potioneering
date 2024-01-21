package tfar.davespotioneering.mixin;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.client.DavesPotioneeringClient;

import java.lang.ref.WeakReference;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel",at = @At("HEAD"))
    private void captureOverrides(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3, CallbackInfoReturnable<BakedModel> cir) {
        DavesPotioneeringClient.itemStack = $$0;
        DavesPotioneeringClient.level = new WeakReference<>($$1);
        DavesPotioneeringClient.player = new WeakReference<>($$2);
        DavesPotioneeringClient.seed = $$3;
    }
}
