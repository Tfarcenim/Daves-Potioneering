package tfar.davespotioneering;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

public class Events {

    public static void potionCooldown(Player player, Level level, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        if (!player.level().isClientSide && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldowns().addCooldown(stack.getItem(), DavesPotioneering.CONFIG.potion_throw_cooldown);
        }
    }

    public static InteractionResult milkCow(Player player, Level e2, InteractionHand hand, Entity clicked, @Nullable EntityHitResult e5) {
        if (clicked instanceof Cow cowEntity && DavesPotioneering.CONFIG.milk) {
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

    public static void onEat(Player player, ItemStack stack) {
        Potion potion = PotionUtils.getPotion(stack);
        for (MobEffectInstance effectInstance : potion.getEffects()) {
            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible()));
        }
    }

    public static InteractionResult afterHit(Player player, Level e2, InteractionHand e3, Entity victim, @Nullable EntityHitResult e5) {

        ItemStack weapon = player.getMainHandItem();

        if (weapon.getItem() instanceof TieredItem) {
            Potion potion = PotionUtils.getPotion(weapon);
            if (potion != Potions.EMPTY) {
                for(MobEffectInstance effectinstance : potion.getEffects()) {
                    ((LivingEntity)victim).addEffect(new MobEffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                }
                if (!player.getAbilities().instabuild)
                    LayeredReinforcedCauldronBlock.useCharge(weapon);
            }
        }
        return InteractionResult.PASS;
    }

    //this is called when the potion is done brewing, we use this instead of the forge event because it has a reference
    // to the blockentity that created the potions
    public static void potionBrew(BlockEntity brewingStandTileEntity, ItemStack ingredient) {
        ((BrewingStandDuck)brewingStandTileEntity).addXp(Util.getBrewXp(ingredient));
    }

    public static void heldItemChangeEvent(Player player) {
        ItemStack stack = player.getMainHandItem();
        if ((stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof SplashPotionItem)) {
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, DavesPotioneering.CONFIG.potion_use_cooldown);
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, DavesPotioneering.CONFIG.potion_use_cooldown);
        }
    }

    //this is called when the player takes a potion from the brewing stand
    public static void playerTakedBrewedPotion(Player player) {
        if (!player.level().isClientSide) {
            AbstractContainerMenu container = player.containerMenu;
            BlockEntity entity = null;
            if (container instanceof BrewingStandMenu) {
                entity = (BrewingStandBlockEntity)((BrewingStandContainerAccess)container).getBrewingStand();
            } else if (container instanceof AdvancedBrewingStandContainer) {
                entity = ((AdvancedBrewingStandContainer)container).blockEntity;
            }

            if (entity != null) {
                ((BrewingStandDuck)entity).dump(player);
            }
        }
    }

    public static boolean canApplyEffect(LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.getUseItem().getItem() instanceof UmbrellaItem) {
                return false;
            }
        }
        return true;
    }
}
