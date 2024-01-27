package tfar.davespotioneering;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Collection;
import java.util.List;

public class PotionUtils2 extends PotionUtils {


    public static CompoundTag saveAllEffects(CompoundTag tag, Potion potion, List<MobEffectInstance> customEffects, Integer color) {
        saveAllEffects(tag, potion, customEffects);
        if (color != null) {
            saveColor(tag,color);
        }
        return tag;
    }

    public static CompoundTag saveAllEffects(CompoundTag tag, Potion potion, List<MobEffectInstance> customEffects) {
        savePotion(tag,potion);
        saveCustomEffects(tag,customEffects);
        return tag;
    }

    private static CompoundTag savePotion(CompoundTag tag, Potion potion) {
        tag.putString(TAG_POTION, BuiltInRegistries.POTION.getKey(potion).toString());
        return tag;
    }

    private static CompoundTag saveCustomEffects(CompoundTag tag, Collection<MobEffectInstance> pEffects) {
        if (!pEffects.isEmpty()) {
            ListTag listtag = tag.getList(TAG_CUSTOM_POTION_EFFECTS, 9);
            for (MobEffectInstance mobeffectinstance : pEffects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }
            tag.put(TAG_CUSTOM_POTION_EFFECTS, listtag);
        }
        return tag;
    }

    public static ItemStack setCustomColor(ItemStack stack,int color) {
        stack.getOrCreateTag().putInt(TAG_CUSTOM_POTION_COLOR,color);
        return stack;
    }

    private static CompoundTag saveColor(CompoundTag tag, int color) {
        tag.putInt(TAG_CUSTOM_POTION_COLOR,color);
        return tag;
    }

    /**
     * Adds the tooltip of the {@code Potion} stored on the {@code ItemStack} along a "durationFactor"
     * @param potionTag the passed {@code CompoundTag}
     * @param pTooltips the passed list of current {@code Component} tooltips
     * @param pDurationFactor the passed durationFactor of the {@code Potion}
     */
    public static void addPotionTooltip(CompoundTag potionTag, List<Component> pTooltips, float pDurationFactor) {
        List<MobEffectInstance> list = getAllEffects(potionTag);
        addPotionTooltip(list,pTooltips,pDurationFactor);
    }
}
