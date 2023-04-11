package tfar.davespotioneering.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.init.ModEffects;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    /**
     * @author Tfar
     * @reason to change potion drinking times
     * @param stack
     * @return
     */
    @Overwrite
    public int getMaxUseTime(ItemStack stack) {
        return 20;//half of 32
    }

    @Inject(method = "hasGlint",at = @At("HEAD"),cancellable = true)
    private void removeGlintFromMilk(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (PotionUtil.getPotionEffects(stack).stream().anyMatch(effectInstance -> effectInstance.getEffectType() == ModEffects.MILK)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "finishUsing",at = @At("HEAD"))
    private void milkify(ItemStack itemStack, World world, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (Util.isMilkified(itemStack)) {
            livingEntity.clearStatusEffects();
        }
    }
}
