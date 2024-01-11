package tfar.davespotioneering.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.init.ModSoundEvents;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GauntletItem extends CGauntletItem implements Perspective {

    public GauntletItem(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.8f, properties);
    }


    // @Override
    //  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    //      return oldStack.getItem() != newStack.getItem();
    //  }

    //  @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return ChatFormatting.GOLD.getColor();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        if (entity instanceof Player && !entity.getCommandSenderWorld().isClientSide()) {
            modifyCooldowns(stack, (cd) -> {
                if (cd > 0) cd -= 1;
                return cd;
            });
        }
    }


    public static void modifyCooldowns(ItemStack gauntlet, Function<Integer, Integer> modifier) {
        CompoundTag info = gauntlet.getOrCreateTag().getCompound("info");
        Tag inbt = info.get("potionCooldownMap");
        if (inbt instanceof ListTag map) {
            if (map.get(0) instanceof IntArrayTag indexArray && map.get(1) instanceof IntArrayTag cooldownArray) {
                if (cooldownArray.isEmpty() || indexArray.isEmpty()) return;
                if (cooldownArray.getAsIntArray().length != indexArray.getAsIntArray().length) return;
                List<Integer> cooldownList = new ArrayList<>();
                List<Integer> indexList = new ArrayList<>();
                for (int i = 0; i < cooldownArray.getAsIntArray().length; i++) {
                    int modified = modifier.apply(cooldownArray.getAsIntArray()[i]);
                    // copying over the cooldown and index to a new list, remove ones that are already expired
                    if (modified > 0) {
                        cooldownList.add(modified);
                        indexList.add(indexArray.getAsIntArray()[i]);
                    }
                }
                IntArrayTag newArray = new IntArrayTag(cooldownList);
                IntArrayTag newArrayIndex = new IntArrayTag(indexList);
                map.set(1, newArray);
                map.set(0, newArrayIndex);
            }
        }
    }

    public static final ResourceLocation ALC_ID = new ResourceLocation(DavesPotioneering.MODID, "item/sprite/potioneer_gauntlet");
    public static final ResourceLocation LIT_ALC_ID = new ResourceLocation(DavesPotioneering.MODID, "item/sprite/lit_potioneer_gauntlet");

    @Override
    public ResourceLocation getGuiModel(boolean active) {
        return active ? LIT_ALC_ID : ALC_ID;
    }
}
