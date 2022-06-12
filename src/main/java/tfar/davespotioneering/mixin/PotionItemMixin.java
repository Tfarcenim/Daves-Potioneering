package tfar.davespotioneering.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.config.ClothConfig;
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


    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     * @author Tfar
     * @reason invasive
     */
    @Overwrite
    public ItemStack finishUsing(ItemStack potion, World worldIn, LivingEntity entityLiving) {
        if (Util.isMilkified(potion)) {
            entityLiving.clearStatusEffects();
        }
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, potion);
        }

        if (!worldIn.isClient) {
            for(StatusEffectInstance effectinstance : PotionUtil.getPotionEffects(potion)) {
                if (effectinstance.getEffectType().isInstant()) {
                    effectinstance.getEffectType().applyInstantEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addStatusEffect(new StatusEffectInstance(effectinstance));
                }
            }
        }

        if (playerentity != null) {
            playerentity.incrementStat(Stats.USED.getOrCreateStat((Item)(Object)this));
            if (!playerentity.getAbilities().creativeMode) {
                potion.decrement(1);
            }
        }

        if (playerentity == null || !playerentity.getAbilities().creativeMode) {
            if (potion.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerentity != null) {
                //the actual change
                if (ClothConfig.return_empty_bottles) {
                    //playerentity.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                    playerentity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    playerentity.dropItem(new ItemStack(Items.GLASS_BOTTLE),false);
                }
            }
        }

        return potion;
    }

}
