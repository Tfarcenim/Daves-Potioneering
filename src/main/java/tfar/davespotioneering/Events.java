package tfar.davespotioneering;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.config.ClothConfig;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

public class Events {

    public static void potionCooldown(PlayerEntity player, World level, Hand interactionHand) {
        ItemStack stack = player.getStackInHand(interactionHand);

        if (!player.world.isClient && stack.getItem() instanceof ThrowablePotionItem) {
            player.getItemCooldownManager().set(stack.getItem(), DavesPotioneering.CONFIG.potion_throw_cooldown);
        }
    }

    public static ActionResult milkCow(PlayerEntity player, World e2, Hand hand, Entity clicked, @Nullable EntityHitResult e5) {
        if (clicked instanceof CowEntity cowEntity && DavesPotioneering.CONFIG.milk) {
            ItemStack itemstack = player.getStackInHand(hand);
            if (itemstack.getItem() == Items.GLASS_BOTTLE && !cowEntity.isBaby()) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                itemstack.decrement(1);
                ItemStack milkBottle = new ItemStack(Items.POTION);
                PotionUtil.setPotion(milkBottle, ModPotions.MILK);
                player.giveItemStack(milkBottle);
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.PASS;
    }

    public static void onEat(PlayerEntity player, ItemStack stack) {
        Potion potion = PotionUtil.getPotion(stack);
        for (StatusEffectInstance effectInstance : potion.getEffects()) {
            player.addStatusEffect(new StatusEffectInstance(effectInstance.getEffectType(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.shouldShowParticles()));
        }
    }

    public static ActionResult afterHit(PlayerEntity player, World e2, Hand e3, Entity victim, @Nullable EntityHitResult e5) {

        ItemStack weapon = player.getMainHandStack();

        if (weapon.getItem() instanceof ToolItem) {
            Potion potion = PotionUtil.getPotion(weapon);
            if (potion != Potions.EMPTY) {
                for(StatusEffectInstance effectinstance : potion.getEffects()) {
                    ((LivingEntity)victim).addStatusEffect(new StatusEffectInstance(effectinstance.getEffectType(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.shouldShowParticles()));
                }
                LayeredReinforcedCauldronBlock.useCharge(weapon);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    //this is called when the potion is done brewing, we use this instead of the forge event because it has a reference
    // to the blockentity that created the potions
    public static void potionBrew(BlockEntity brewingStandTileEntity, ItemStack ingredient) {
        ((BrewingStandDuck)brewingStandTileEntity).addXp(Util.getBrewXp(ingredient));
    }

    public static void heldItemChangeEvent(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if ((stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof SplashPotionItem)) {
            player.getItemCooldownManager().set(Items.SPLASH_POTION, DavesPotioneering.CONFIG.potion_use_cooldown);
            player.getItemCooldownManager().set(Items.LINGERING_POTION, DavesPotioneering.CONFIG.potion_use_cooldown);
        }
    }

    //this is called when the player takes a potion from the brewing stand
    public static void playerTakedBrewedPotion(PlayerEntity player) {
        if (!player.world.isClient) {
            ScreenHandler container = player.currentScreenHandler;
            BlockEntity entity = null;
            if (container instanceof BrewingStandScreenHandler) {
                entity = (BrewingStandBlockEntity)((BrewingStandContainerAccess)container).getInventory();
            } else if (container instanceof AdvancedBrewingStandContainer) {
                entity = ((AdvancedBrewingStandContainer)container).blockEntity;
            }

            if (entity != null) {
                ((BrewingStandDuck)entity).dump(player);
            }
        }
    }

    public static boolean canApplyEffect(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (player.getActiveItem().getItem() instanceof UmbrellaItem) {
                return false;
            }
        }
        return true;
    }
}
