package tfar.davespotioneering.mixin;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.ModConfig;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "calcModifierDamage",at = @At("HEAD"),cancellable = true)
    private void modifyDamageCalc(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.Server.magic_protection.get() && (Object)this == Enchantments.ALL_DAMAGE_PROTECTION) {
            if (source.isMagic()) {
                cir.setReturnValue(0);
            }
        }
    }
}
