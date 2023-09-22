package tfar.davespotioneering.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.ClientHooks;

import java.util.Map;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Inject(method = "loadTopLevel",at = @At("RETURN"))
    private void injectedModels(ModelResourceLocation modelId, CallbackInfo ci) {
        if (modelId.getPath().contains("trident_in_hand")) {
            ClientHooks.injectCustomModels((ModelBakery)(Object)this, unbakedCache, topLevelModels);
        }
    }
}
