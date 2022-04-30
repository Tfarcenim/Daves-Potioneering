package tfar.davespotioneering.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.ModConfig;
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
    public int getUseDuration(ItemStack stack) {
        return 20;//half of 32
    }

    @Inject(method = "hasEffect",at = @At("HEAD"),cancellable = true)
    private void removeGlintFromMilk(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (PotionUtils.getMobEffects(stack).stream().anyMatch(effectInstance -> effectInstance.getEffect() == ModEffects.MILK)) {
            cir.setReturnValue(false);
        }
    }


    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     * @author Tfar
     */
    @Overwrite
    public ItemStack onItemUseFinish(ItemStack potion, World worldIn, LivingEntity entityLiving) {
        if (Util.isMilkified(potion)) {
            entityLiving.removeAllEffects();
        }
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, potion);
        }

        if (!worldIn.isClientSide) {
            for(EffectInstance effectinstance : PotionUtils.getMobEffects(potion)) {
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new EffectInstance(effectinstance));
                }
            }
        }

        if (playerentity != null) {
            playerentity.awardStat(Stats.ITEM_USED.get((Item)(Object)this));
            if (!playerentity.abilities.instabuild) {
                potion.shrink(1);
            }
        }

        if (playerentity == null || !playerentity.abilities.instabuild) {
            if (potion.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerentity != null) {
                //the actual change
                if (ModConfig.Server.return_empty_bottles.get()) {
                    //playerentity.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                    playerentity.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    playerentity.drop(new ItemStack(Items.GLASS_BOTTLE),false);
                }
            }
        }

        return potion;
    }

}
