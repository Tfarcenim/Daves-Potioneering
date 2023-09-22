package tfar.davespotioneering.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;

@Mixin(ItemOverrides.class)
public class ItemOverrideListMixin {

    @Inject(method = "apply",at = @At(value = "RETURN",ordinal = 1),locals = LocalCapture.CAPTURE_FAILHARD)
    private void validModel(BakedModel itempropertyfunction, ItemStack j, ClientLevel level, LivingEntity entity, int i1, CallbackInfoReturnable<BakedModel> cir, Item item, int i, float[] afloat, ItemOverrides.BakedOverride[] var9, int var10, int var11, ItemOverrides.BakedOverride itemoverrides$bakedoverride, BakedModel bakedmodel) {
        DoubleGeoItemStackRenderer.override.set(afloat[0]);
    }
}
