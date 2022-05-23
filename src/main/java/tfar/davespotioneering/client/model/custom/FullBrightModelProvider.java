package tfar.davespotioneering.client.model.custom;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.DavesPotioneering;

public class FullBrightModelProvider implements ModelResourceProvider {
    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
        if (resourceId.getNamespace().equals(DavesPotioneering.MODID) && resourceId.getPath().contains("lit_potioneer_gauntlet")) {
            UnbakedModel unbakedModel = context.loadModel(resourceId);
            return new FullBrightModel(unbakedModel, false);
        }
        return null;
    }
}
