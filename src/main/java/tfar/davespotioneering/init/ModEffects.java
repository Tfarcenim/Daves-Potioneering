package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.effect.MilkEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModEffects {

    private static List<MobEffect> MOD_EFFECTS;

    public static final MobEffect MILK = new MilkEffect(MobEffectCategory.NEUTRAL,0xffffff);

    public static void register() {
        for (Field field : ModEffects.class.getFields()) {
            try {
                if (field.get(null) instanceof MobEffect effect) {
                    Registry.register(BuiltInRegistries.MOB_EFFECT,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),effect);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
