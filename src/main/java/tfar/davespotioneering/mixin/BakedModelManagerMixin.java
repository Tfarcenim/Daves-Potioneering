package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.duck.ModelManagerDuck;

import java.util.Map;

@Mixin(BakedModelManager.class)
abstract class BakedModelManagerMixin implements ModelManagerDuck {

    @Shadow private BakedModel missingModel;

    @Shadow private Map<Identifier, BakedModel> models;

    @Override
    public BakedModel getSpecialModel(Identifier rl) {
        return models.getOrDefault(rl,missingModel);
    }


    @Inject(method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void manipulateModels(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        //ClientHooks.onModelBake((BakedModelManager) (Object)this,modelLoader.getBakedModelMap(),modelLoader);
    }
}
