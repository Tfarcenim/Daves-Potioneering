package tfar.davespotioneering.init;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.effect.MilkEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModEffects {

    public static final Effect MILK = new MilkEffect(EffectType.NEUTRAL,0xffffff);

    private static List<Effect> MOD_EFFECTS;

    public static void register(RegistryEvent.Register<Effect> e) {
        for (Field field : ModEffects.class.getFields()) {
            try {
                if (field.get(null) instanceof Effect) {
                    Effect effect = (Effect)field.get(null);
                    effect.setRegistryName(field.getName().toLowerCase(Locale.ROOT));
                       e.getRegistry().register(effect);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
