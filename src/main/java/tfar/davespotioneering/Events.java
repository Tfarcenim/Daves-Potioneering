package tfar.davespotioneering;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

public class Events {
    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        PlayerEntity player = e.getPlayer();
        if (!player.world.isRemote && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldownTracker().setCooldown(stack.getItem(), ModConfig.Server.potion_throw_cooldown.get());
        }
    }

    public static void milkCow(PlayerInteractEvent.EntityInteractSpecific e) {
        Entity clicked = e.getTarget();
        PlayerEntity player = e.getPlayer();
        if (clicked instanceof CowEntity && ModConfig.Server.milk.get()) {
            CowEntity cowEntity = (CowEntity)clicked;
            ItemStack itemstack = player.getHeldItem(e.getHand());
            if (itemstack.getItem() == Items.GLASS_BOTTLE && !cowEntity.isChild()) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                itemstack.shrink(1);
                ItemStack milkBottle = new ItemStack(Items.POTION);
                PotionUtils.addPotionToItemStack(milkBottle, ModPotions.MILK);
                player.addItemStackToInventory(milkBottle);
            }
        }
    }

    public static void afterHit(LivingDamageEvent e) {
        LivingEntity victim = e.getEntityLiving();

        DamageSource source = e.getSource();

        Entity trueSource = source.getTrueSource();

        if (trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)trueSource;

            ItemStack weapon = attacker.getHeldItemMainhand();

            if (weapon.getItem() instanceof TieredItem) {
                Potion potion = PotionUtils.getPotionFromItem(weapon);
                if (potion != Potions.EMPTY) {
                    for(EffectInstance effectinstance : potion.getEffects()) {
                        victim.addPotionEffect(new EffectInstance(effectinstance.getPotion(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles()));
                    }
                    ReinforcedCauldronBlock.useCharge(weapon);
                }
            }
        }
    }

    //this is called when the potion is done brewing, we use this instead of the forge event because it has a reference
    // to the blockentity that created the potions
    public static void potionBrew(TileEntity brewingStandTileEntity, ItemStack ingredient) {
        ((BrewingStandDuck)brewingStandTileEntity).addXp(Util.getBrewXp(ingredient));
    }

    public static void heldItemChangeEvent(PlayerEntity player) {
        ItemStack stack = player.getHeldItemMainhand();
        if ((stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof SplashPotionItem)) {
            player.getCooldownTracker().setCooldown(Items.SPLASH_POTION, ModConfig.Server.potion_switch_cooldown.get());
            player.getCooldownTracker().setCooldown(Items.LINGERING_POTION, ModConfig.Server.potion_switch_cooldown.get());
        }
    }

    public static void onEat(PlayerEntity player, ItemStack stack) {
        Potion potion = PotionUtils.getPotionFromItem(stack);
        for (EffectInstance effectInstance : potion.getEffects()) {
            player.addPotionEffect(new EffectInstance(effectInstance.getPotion(), Math.max(effectInstance.getDuration() / 8, 1), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.doesShowParticles()));
        }
    }

    //this is called when the player takes a potion from the brewing stand
    public static void playerBrew(PlayerBrewedPotionEvent e) {
        PlayerEntity player = e.getPlayer();
        if (!player.world.isRemote) {
            Container container = player.openContainer;
            TileEntity entity = null;
            if (container instanceof BrewingStandContainer) {
                entity = (BrewingStandTileEntity)((BrewingStandContainerAccess)container).getTileBrewingStand();
            } else if (container instanceof AdvancedBrewingStandContainer) {
                entity = ((AdvancedBrewingStandContainer)container).blockEntity;
            }

            if (entity != null) {
                ((BrewingStandDuck)entity).dump(player);
            }
        }
    }

    public static void canApplyEffect(PotionEvent.PotionApplicableEvent e) {
        LivingEntity entity = e.getEntityLiving();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (player.getActiveItemStack().getItem() instanceof UmbrellaItem) {
                e.setResult(Event.Result.DENY);
            }
        }
    }

    public static void effectColor(PotionColorCalculationEvent e) {
        int old = e.getColor();
        if (old == 0) {
            for(EffectInstance effectinstance : e.getEffects()) {
                if (effectinstance.equals(ModPotions.INVIS_2)) {
                    int k = effectinstance.getPotion().getLiquidColor();
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
