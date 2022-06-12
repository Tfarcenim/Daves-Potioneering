package tfar.davespotioneering.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.effect.MilkEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModEffects {

    private static List<StatusEffect> MOD_EFFECTS;

    public static final StatusEffect MILK = new MilkEffect(StatusEffectCategory.NEUTRAL,0xffffff);

    public static void register() {
        for (Field field : ModEffects.class.getFields()) {
            try {
                if (field.get(null) instanceof StatusEffect effect) {
                    Registry.register(Registry.STATUS_EFFECT,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),effect);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
