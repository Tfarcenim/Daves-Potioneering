package tfar.davespotioneering.mixin;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemOverrides.class)
public interface ItemOverridesAccessor {

    @Accessor
    ItemOverrides.BakedOverride[] getOverrides();

}
