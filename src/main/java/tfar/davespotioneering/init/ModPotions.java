package tfar.davespotioneering.init;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModPotions {

    private static List<Potion> MOD_POTIONS;

    public static final Potion MILK = new Potion(new StatusEffectInstance(ModEffects.MILK));

    public static final StatusEffectInstance INVIS_2 =  new StatusEffectInstance(StatusEffects.INVISIBILITY, 1800,1,false,false);

    public static final Potion STRONG_INVISIBILITY = new Potion("invisibility",INVIS_2);

    public static void register() {
        for (Field field : ModPotions.class.getFields()) {
            try {
                if (field.get(null) instanceof Potion) {
                    Potion potion = (Potion)field.get(null);
                    Registry.register(Registry.POTION,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),potion);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
