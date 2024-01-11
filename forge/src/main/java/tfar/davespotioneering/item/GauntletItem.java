package tfar.davespotioneering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.TickEvent;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

public class GauntletItem extends CGauntletItem {

    public GauntletItem(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.8f, properties);
    }

    @Override
    public int getDamage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        double blaze = 0;
        if (tag != null) {
            CompoundTag info = tag.getCompound(INFO);
            blaze = info.getInt(BLAZE);
        }
        return CPotionInjectorMenu.BLAZE_CAP - (int) blaze;
    }


    public static void tickCooldowns(TickEvent.PlayerTickEvent event) {
        tickCooldownsCommon(event.player);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }


    @Override
    public int getBarColor(ItemStack stack) {
        return ChatFormatting.GOLD.getColor();
    }

}