package tfar.davespotioneering.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.effect.MilkEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModEffects {

    private static List<MobEffect> MOD_EFFECTS;

    public static final MobEffect MILK = new MilkEffect(MobEffectCategory.NEUTRAL,0xffffff);

    public static void register(RegistryEvent.Register<MobEffect> e) {
        for (Field field : ModEffects.class.getFields()) {
            try {
                if (field.get(null) instanceof MobEffect) {
                    MobEffect effect = (MobEffect)field.get(null);
                    effect.setRegistryName(field.getName().toLowerCase(Locale.ROOT));
                       e.getRegistry().register(effect);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
