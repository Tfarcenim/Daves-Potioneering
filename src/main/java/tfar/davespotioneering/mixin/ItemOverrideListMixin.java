package tfar.davespotioneering.mixin;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;

import javax.annotation.Nullable;

@Mixin(ItemOverrideList.class)
public class ItemOverrideListMixin {

    @Inject(method = "getOverrideModel",at = @At(value = "RETURN",ordinal = 1),locals = LocalCapture.CAPTURE_FAILHARD)
    private void validModel(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity,
                            CallbackInfoReturnable<IBakedModel> cir, int i, ItemOverride itemoverride, IBakedModel ibakedmodel) {
        DoubleGeoItemStackRenderer.override.set(i);
    }
}
