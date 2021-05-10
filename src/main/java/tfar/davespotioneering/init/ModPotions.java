package tfar.davespotioneering.init;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModPotions {

    public static final Potion MILK = new Potion(new EffectInstance(ModEffects.MILK));

    public static final Potion STRONG_INVISIBILITY = new Potion("invisibility", new EffectInstance(Effects.INVISIBILITY, 1800,1,false,false));

    private static List<Potion> MOD_POTIONS;

    public static void register(RegistryEvent.Register<Potion> e) {
        for (Field field : ModPotions.class.getFields()) {
            try {
                if (field.get(null) instanceof Potion) {
                    Potion potion = (Potion)field.get(null);
                    potion.setRegistryName(field.getName().toLowerCase(Locale.ROOT));
                       e.getRegistry().register(potion);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
