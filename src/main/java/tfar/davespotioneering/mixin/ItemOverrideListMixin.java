package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
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

@Mixin(ModelOverrideList.class)
public class ItemOverrideListMixin {

    @Inject(method = "apply",at = @At(value = "RETURN",ordinal = 1),locals = LocalCapture.CAPTURE_FAILHARD)
    private void validModel(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity,
                            CallbackInfoReturnable<BakedModel> cir, int i, ModelOverride itemoverride, BakedModel ibakedmodel) {
        DoubleGeoItemStackRenderer.override.set(i);
    }
}
