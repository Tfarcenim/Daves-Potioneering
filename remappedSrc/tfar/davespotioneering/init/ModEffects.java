package tfar.davespotioneering.init;

import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.effect.MilkEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEffects {

    private static List<StatusEffect> MOD_EFFECTS;

    public static final StatusEffect MILK = new MilkEffect(StatusEffectType.NEUTRAL,0xffffff);

    public static void register() {
        for (Field field : ModEffects.class.getFields()) {
            try {
                if (field.get(null) instanceof StatusEffect) {
                    StatusEffect effect = (StatusEffect)field.get(null);
                    Registry.register(Registry.STATUS_EFFECT,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),effect);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
