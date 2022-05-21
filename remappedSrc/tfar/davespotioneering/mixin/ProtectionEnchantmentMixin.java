package tfar.davespotioneering.mixin;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.ModConfig;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "getDamageProtection",at = @At("HEAD"),cancellable = true)
    private void modifyDamageCalc(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.Server.magic_protection && (Object)this == Enchantments.PROTECTION) {
            if (source.getMagic()) {
                cir.setReturnValue(0);
            }
        }
    }
}
