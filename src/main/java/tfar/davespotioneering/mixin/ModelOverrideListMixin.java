package tfar.davespotioneering.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ModelOverrideList.class)
public interface ModelOverrideListMixin {

    @Accessor
    List<BakedModel> getModels();

}
