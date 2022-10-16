package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.RegisterEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModPotions {

    private static List<Potion> MOD_POTIONS;

    public static final Potion MILK = new Potion(new MobEffectInstance(ModEffects.MILK));

    public static final MobEffectInstance INVIS_2 =  new MobEffectInstance(MobEffects.INVISIBILITY, 1800,1,false,false);

    public static final Potion STRONG_INVISIBILITY = new Potion("invisibility",INVIS_2);

    public static void register(RegisterEvent e) {
        for (Field field : ModPotions.class.getFields()) {
            try {
                if (field.get(null) instanceof Potion) {
                    Potion potion = (Potion)field.get(null);
                    potion.setRegistryName(field.getName().toLowerCase(Locale.ROOT));
                       e.register(ResourceKey.create(new ResourceLocation("potion",field.getName().toLowerCase(Locale.ROOT))));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
