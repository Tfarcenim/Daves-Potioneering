package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;

@Mixin(ModelOverrideList.class)
public class ItemOverrideListMixin {

    @Inject(method = "apply",at = @At(value = "RETURN",ordinal = 1),locals = LocalCapture.CAPTURE_FAILHARD)
    private void validModel(BakedModel itempropertyfunction, ItemStack j, ClientWorld level, LivingEntity entity, int i1, CallbackInfoReturnable<BakedModel> cir, Item item, int i, float[] afloat, ModelOverrideList.BakedOverride[] var9, int var10, int var11, ModelOverrideList.BakedOverride itemoverrides$bakedoverride, BakedModel bakedmodel) {
        DoubleGeoItemStackRenderer.override.set(afloat[0]);
    }
}
