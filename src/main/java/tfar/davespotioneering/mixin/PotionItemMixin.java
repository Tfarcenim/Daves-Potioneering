package tfar.davespotioneering.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
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

    @Inject(method = "isFoil",at = @At("HEAD"),cancellable = true)
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
    public ItemStack finishUsingItem(ItemStack potion, Level worldIn, LivingEntity entityLiving) {
        if (Util.isMilkified(potion)) {
            entityLiving.removeAllEffects();
        }
        Player playerentity = entityLiving instanceof Player ? (Player)entityLiving : null;
        if (playerentity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)playerentity, potion);
        }

        if (!worldIn.isClientSide) {
            for(MobEffectInstance effectinstance : PotionUtils.getMobEffects(potion)) {
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new MobEffectInstance(effectinstance));
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
                if (ModConfig.Server.return_empty_bottles) {
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
