package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
}
