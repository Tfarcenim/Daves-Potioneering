package tfar.davespotioneering.mixin;

import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.util.perf.Profiler;
import tfar.davespotioneering.client.ClientHooks;
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


    @Inject(method = "loadModels",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",ordinal = 1))
    private void manipulateModels(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        ClientHooks.onModelBake(modelBakery.getBakedTopLevelModels(), modelBakery);
    }
}
