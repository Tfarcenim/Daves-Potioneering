package tfar.davespotioneering;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.List;

public class PotionUtils2 extends PotionUtil {

    public static NbtCompound saveAllEffects(NbtCompound tag, Potion potion, List<StatusEffectInstance> customEffects, Integer color) {
        saveAllEffects(tag, potion, customEffects);
        if (color != null) {
            saveColor(tag,color);
        }
        return tag;
    }

    public static NbtCompound saveAllEffects(NbtCompound tag, Potion potion, List<StatusEffectInstance> customEffects) {
        savePotion(tag,potion);
        saveCustomEffects(tag,customEffects);
        return tag;
    }

    private static NbtCompound saveColor(NbtCompound tag, int color) {
        tag.putInt(CUSTOM_POTION_COLOR_KEY,color);
        return tag;
    }


    private static NbtCompound savePotion(NbtCompound tag,Potion potion) {
        tag.putString(POTION_KEY, Registry.POTION.getKey(potion).toString());
        return tag;
    }

    private static NbtCompound saveCustomEffects(NbtCompound tag, Collection<StatusEffectInstance> pEffects) {
        if (!pEffects.isEmpty()) {
            NbtList listtag = tag.getList(CUSTOM_POTION_EFFECTS_KEY, 9);
            for (StatusEffectInstance mobeffectinstance : pEffects) {
                listtag.add(mobeffectinstance.writeNbt(new NbtCompound()));
            }
            tag.put(CUSTOM_POTION_EFFECTS_KEY, listtag);
        }
        return tag;
    }

    private static final Text NO_EFFECT = new TranslatableText("effect.none").formatted(Formatting.GRAY);

}
