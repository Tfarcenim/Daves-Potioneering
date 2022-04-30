package tfar.davespotioneering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.item.*;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.client.GauntletHUDMovementGui;
import tfar.davespotioneering.duck.BrewingStandDuck;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.SplashPotionItem;

public class Events {

    public static void switchGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.SURVIVAL && event.getCurrentGameMode() == GameType.CREATIVE && GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR) {
            GauntletHUD.hudInstance.y = GauntletHUDMovementGui.getFixedPositionValue(Minecraft.getInstance().getWindow().getGuiScaledHeight() - 42 - 40, false);
        }
        if (event.getNewGameMode() == GameType.CREATIVE && event.getCurrentGameMode() == GameType.SURVIVAL && GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR) {
            GauntletHUD.hudInstance.y = GauntletHUDMovementGui.getFixedPositionValue(Minecraft.getInstance().getWindow().getGuiScaledHeight() - 42 - 25, false);
        }
    }

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        Player player = e.getPlayer();
        if (!player.level.isClientSide && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldowns().addCooldown(stack.getItem(), ModConfig.Server.potion_cooldown);
        }
    }

    public static void milkCow(PlayerInteractEvent.EntityInteractSpecific e) {
        Entity clicked = e.getTarget();
        Player player = e.getPlayer();
        if (clicked instanceof Cow) {
            Cow cowEntity = (Cow)clicked;
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
        LivingEntity victim = e.getEntityLiving();

        DamageSource source = e.getSource();

        Entity trueSource = source.getEntity();

        if (trueSource instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)trueSource;

            ItemStack weapon = attacker.getMainHandItem();

            if (weapon.getItem() instanceof TieredItem) {
                Potion potion = PotionUtils.getPotion(weapon);
                if (potion != Potions.EMPTY) {
                    for(MobEffectInstance effectinstance : potion.getEffects()) {
                        victim.addEffect(new MobEffectInstance(effectinstance.getEffect(), Math.max(effectinstance.getDuration() / 8, 1), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                    }
                    ReinforcedCauldronBlock.useCharge(weapon);
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
            player.getCooldowns().addCooldown(Items.SPLASH_POTION, ModConfig.Server.potion_cooldown);
            player.getCooldowns().addCooldown(Items.LINGERING_POTION, ModConfig.Server.potion_cooldown);
        }
    }

    //this is called when the player takes a potion from the brewing stand
    public static void playerBrew(PlayerBrewedPotionEvent e) {
        Player player = e.getPlayer();
        if (!player.level.isClientSide) {
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

    public static void canApplyEffect(PotionEvent.PotionApplicableEvent e) {
        LivingEntity entity = e.getEntityLiving();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.getUseItem().getItem() instanceof UmbrellaItem) {
                e.setResult(Event.Result.DENY);
            }
        }
    }
}
