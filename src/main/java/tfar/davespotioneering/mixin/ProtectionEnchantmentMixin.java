package tfar.davespotioneering.mixin;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.config.ClothConfig;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "getProtectionAmount",at = @At("HEAD"),cancellable = true)
    private void modifyDamageCalc(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (ClothConfig.magic_protection && (Object)this == Enchantments.PROTECTION) {
            if (source.getMagic()) {
                cir.setReturnValue(0);
            }
        }
    }
}
