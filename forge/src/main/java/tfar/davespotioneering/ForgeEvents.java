package tfar.davespotioneering;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import tfar.davespotioneering.block.CLayeredReinforcedCauldronBlock;
import tfar.davespotioneering.client.DavesPotioneeringClient;
import tfar.davespotioneering.init.ModPotions;

public class ForgeEvents {

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        Player player = e.getEntity();
        if (!player.level().isClientSide && stack.getItem() instanceof ThrowablePotionItem) {
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
                    CLayeredReinforcedCauldronBlock.useCharge(weapon);
                }
            }
        }
    }

    public static void playerBrew(PlayerBrewedPotionEvent e) {
        DavesPotioneering.playerTakeBrewedPotion(e.getEntity());
    }

    public static void canApplyEffect(MobEffectEvent.Applicable e) {
        if (!DavesPotioneering.canApplyEffect(e.getEntity())) {
            e.setResult(Event.Result.DENY);
        }
    }

    public static void serverStart(ServerStartingEvent e) {
        Util.setStackSize(Items.POTION,ModConfig.Server.potion_stack_size.get());
        Util.setStackSize(Items.SPLASH_POTION,ModConfig.Server.splash_potion_stack_size.get());
        Util.setStackSize(Items.LINGERING_POTION,ModConfig.Server.lingering_potion_stack_size.get());
    }

    public static void effectColor(PotionColorCalculationEvent e) {
        int old = e.getColor();
        if (old == 0) {
            for(MobEffectInstance effectinstance : e.getEffects()) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    e.setColor(DavesPotioneeringClient.computeinvis2Color(effectinstance));
                }
            }
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::potionCooldown);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::milkCow);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::afterHit);

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::playerBrew);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::canApplyEffect);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::effectColor);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::serverStart);
    }
}
