package tfar.davespotioneering;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.block.CLayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;

public class FabricEvents {

    public static void potionCooldown(Player player, Level level, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        if (!player.level().isClientSide && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldowns().addCooldown(stack.getItem(), DavesPotioneeringFabric.CONFIG.potion_throw_cooldown);
        }
    }

    public static InteractionResult milkCow(Player player, Level e2, InteractionHand hand, Entity clicked, @Nullable EntityHitResult e5) {
        if (clicked instanceof Cow cowEntity && DavesPotioneeringFabric.CONFIG.milk) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (itemstack.getItem() == Items.GLASS_BOTTLE && !cowEntity.isBaby()) {
                player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                itemstack.shrink(1);
                ItemStack milkBottle = new ItemStack(Items.POTION);
                PotionUtils.setPotion(milkBottle, ModPotions.MILK);
                player.addItem(milkBottle);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult afterHit(Player player, Level e2, InteractionHand e3, Entity victim, @Nullable EntityHitResult e5) {
        if (victim instanceof LivingEntity livingVictim) {
            DavesPotioneering.afterHit(player,livingVictim);
        }

        return InteractionResult.PASS;
    }
}
