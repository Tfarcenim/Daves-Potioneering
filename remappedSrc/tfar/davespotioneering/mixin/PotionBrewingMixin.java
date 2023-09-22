package tfar.davespotioneering.mixin;

import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.DavesPotioneering;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @Inject(method = "registerDefaults", at = @At("RETURN"))
    private static void addPotions(CallbackInfo ci) {
        DavesPotioneering.addPotions();
    }
}
