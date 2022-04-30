package tfar.davespotioneering.mixin;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;

import javax.annotation.Nullable;

@Mixin(ItemOverrides.class)
public class ItemOverrideListMixin {

    @Inject(method = "getOverrideModel",at = @At(value = "RETURN",ordinal = 1),locals = LocalCapture.CAPTURE_FAILHARD)
    private void validModel(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity,
                            CallbackInfoReturnable<BakedModel> cir, int i, ItemOverride itemoverride, BakedModel ibakedmodel) {
        DoubleGeoItemStackRenderer.override.set(i);
    }
}
