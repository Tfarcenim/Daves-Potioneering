package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.ClientHooks;

import java.util.Map;

@Mixin(ModelLoader.class)
public class ModelBakeryMixin {

    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;

    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;

    @Inject(method = "addModel",at = @At("RETURN"))
    private void injectedModels(ModelIdentifier modelId, CallbackInfo ci) {
        if (modelId.getPath().contains("trident_in_hand")) {
            ClientHooks.injectCustomModels((ModelLoader)(Object)this,unbakedModels,modelsToBake);
        }
    }
}
