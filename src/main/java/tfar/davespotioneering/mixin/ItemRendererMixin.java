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
import tfar.davespotioneering.client.ClientEvents;

import java.lang.ref.WeakReference;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel",at = @At("HEAD"))
    private void validModel(ItemStack stack, Level pLevel, LivingEntity living, int seed, CallbackInfoReturnable<BakedModel> cir) {
        ClientEvents.itemStack = stack;
        ClientEvents.level = new WeakReference<>(pLevel);
        ClientEvents.player = new WeakReference<>(living);
        ClientEvents.seed = seed;
    }
}
