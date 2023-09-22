package tfar.davespotioneering.mixin;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.davespotioneering.duck.ModelManagerDuck;

import java.util.Map;

@Mixin(ModelManager.class)
abstract class BakedModelManagerMixin implements ModelManagerDuck {

    @Shadow private BakedModel missingModel;

    @Shadow private Map<ResourceLocation, BakedModel> bakedRegistry;

    @Override
    public BakedModel getSpecialModel(ResourceLocation rl) {
        return bakedRegistry.getOrDefault(rl,missingModel);
    }


   // @Inject(method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
   //         at = @At(value = "INVOKE",target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
   // private void manipulateModels(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
  //      //ClientHooks.onModelBake((BakedModelManager) (Object)this,modelLoader.getBakedModelMap(),modelLoader);
 //   }
}
