package tfar.davespotioneering.mixin;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemOverrides.BakedOverride.class)
public interface BakedOverrideAccessor {

    @Accessor
    BakedModel getModel();

    @Accessor @Mutable
    void setModel(BakedModel model);

}
