package tfar.davespotioneering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.item.*;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
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
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.mixin.BrewingStandContainerAccess;

public class Events {

    public static void switchGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.SURVIVAL && event.getCurrentGameMode() == GameType.CREATIVE && GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR) {
            GauntletHUD.hudInstance.y = GauntletHUDMovementGui.getFixedPositionValue(Minecraft.getInstance().getMainWindow().getScaledHeight() - 42 - 40, false);
        }
        if (event.getNewGameMode() == GameType.CREATIVE && event.getCurrentGameMode() == GameType.SURVIVAL && GauntletHUD.hudInstance.preset == GauntletHUD.HudPresets.ABOVE_HOTBAR) {
            GauntletHUD.hudInstance.y = GauntletHUDMovementGui.getFixedPositionValue(Minecraft.getInstance().getMainWindow().getScaledHeight() - 42 - 25, false);
        }
    }

    public static void potionCooldown(PlayerInteractEvent.RightClickItem e) {
        ItemStack stack = e.getItemStack();
        PlayerEntity player = e.getPlayer();
        if (!player.world.isRemote && stack.getItem() instanceof ThrowablePotionItem) {
            player.getCooldownTracker().setCooldown(stack.getItem(), ModConfig.Server.potion_cooldown);
        }
    }

    public static void milkCow(PlayerInteractEvent.EntityInteractSpecific e) {
        Entity clicked = e.getTarget();
        PlayerEntity player = e.getPlayer();
        if (clicked instanceof CowEntity) {
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
            player.getCooldownTracker().setCooldown(Items.SPLASH_POTION, ModConfig.Server.potion_cooldown);
            player.getCooldownTracker().setCooldown(Items.LINGERING_POTION, ModConfig.Server.potion_cooldown);
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
}
