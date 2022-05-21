package tfar.davespotioneering.mixin;

import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.DavesPotioneering;

@Mixin(BrewingRecipeRegistry.class)
public class PotionBrewingMixin {
    @Inject(method = "registerDefaults", at = @At("RETURN"))
    private static void addPotions(CallbackInfo ci) {
        DavesPotioneering.addPotions();
    }
}
