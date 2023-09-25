package tfar.davespotioneering;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandMenu;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

import java.util.List;

public class Events {

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        Player player = e.getEntity();
        if (!player.level.isClientSide && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldowns().addCooldown(stack.getItem(), ModConfig.Server.potion_throw_cooldown.get());
        }
    }

    public static void milkCow(PlayerInteractEvent.EntityInteractSpecific e) {
        Entity clicked = e.getTarget();
        Player player = e.getEntity();
        if (ModConfig.Server.milk.get() && clicked instanceof Cow cowEntity) {
            ItemStack itemstack = player.getItemInHand(e.getHand());
            if (itemstack.getItem() == Items.GLASS_BOTTLE && !cowEntity.isBaby()) {
                player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                itemstack.shrink(1);
                ItemStack milkBottle = new ItemStack(Items.POTION);
                PotionUtils.setPotion(milkBottle, ModPotions.MILK);
                player.addItem(milkBottle);
            }
        }
    }

    public static void afterHit(LivingDamageEvent e) {
        LivingEntity victim = e.getEntity();

        DamageSource source = e.getSource();

        Entity trueSource = source.getEntity();

        if (trueSource instanceof LivingEntity attacker) {

            ItemStack weapon = attacker.getMainHandItem();

            if (weapon.getItem() instanceof TieredItem) {
                Potion potion = PotionUtils.getPotion(weapon);
                if (potion != Potions.EMPTY) {
                    for(MobEffectInstance effectinstance : potion.getEffects()) {
                        victim.addEffect(new MobEffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                    }
                    LayeredReinforcedCauldronBlock.useCharge(weapon);
                }
            }
        }
    }

    //this is called when the potion is done brewing, we use this instead of the forge event because it has a reference
    // to the blockentity that created the potions
    public static void potionBrew(BlockEntity brewingStandTileEntity, ItemStack ingredient) {
        ((BrewingStandDuck)brewingStandTileEntity).addXp(Util.getBrewXp(ingredient));
    }

    public static void heldItemChangeEvent(Player player) {
        ItemStack stack = player.getMainHandItem();
        if ((stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof SplashPotionItem)) {
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, ModConfig.Server.potion_switch_cooldown.get());
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, ModConfig.Server.potion_switch_cooldown.get());
        }
    }

    public static void onEat(Player player, ItemStack stack) {
        List<MobEffectInstance> mobEffectInstances = PotionUtils.getMobEffects(stack);
        for (MobEffectInstance effectInstance : mobEffectInstances) {
            player.addEffect(new MobEffectInstance(effectInstance.getEffect(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.showIcon()));
        }
    }

    //this is called when the player takes a potion from the brewing stand
    public static void playerBrew(PlayerBrewedPotionEvent e) {
        Player player = e.getEntity();
        if (!player.level.isClientSide) {
            AbstractContainerMenu container = player.containerMenu;
            BlockEntity entity = null;
            if (container instanceof BrewingStandMenu) {
                entity = (BrewingStandBlockEntity)((BrewingStandContainerAccess)container).getBrewingStand();
            } else if (container instanceof AdvancedBrewingStandMenu advancedBrewingStandMenu) {
                entity = advancedBrewingStandMenu.blockEntity;
            }

            if (entity != null) {
                ((BrewingStandDuck)entity).dump(player);
            }
        }
    }

    public static void canApplyEffect(MobEffectEvent.Applicable e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Player player) {
            if (player.getUseItem().getItem() instanceof UmbrellaItem) {
                e.setResult(Event.Result.DENY);
            }
        }
    }

    public static void effectColor(PotionColorCalculationEvent e) {
        int old = e.getColor();
        if (old == 0) {
            for(MobEffectInstance effectinstance : e.getEffects()) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    int k = effectinstance.getEffect().getColor();
                    int l = 1;
                    float r = (float)(l * (k >> 16 & 255)) / 255.0F;
                    float g = (float)(l * (k >> 8 & 255)) / 255.0F;
                    float b = (float)(l * (k & 255)) / 255.0F;

                    r = r * 255.0F;
                    g = g * 255.0F;
                    b = b * 255.0F;
                    e.setColor((int)r << 16 | (int)g << 8 | (int)b);
                }
            }
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(Events::potionCooldown);
        MinecraftForge.EVENT_BUS.addListener(Events::milkCow);
        MinecraftForge.EVENT_BUS.addListener(Events::afterHit);

        MinecraftForge.EVENT_BUS.addListener(Events::playerBrew);
        MinecraftForge.EVENT_BUS.addListener(Events::canApplyEffect);
        MinecraftForge.EVENT_BUS.addListener(Events::effectColor);
    }
}
