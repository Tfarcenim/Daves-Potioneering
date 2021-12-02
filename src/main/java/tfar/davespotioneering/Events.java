package tfar.davespotioneering;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import tfar.davespotioneering.block.ReinforcedCauldronBlock;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.client.GauntletHUDMovementGui;
import tfar.davespotioneering.init.ModPotions;

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
}
