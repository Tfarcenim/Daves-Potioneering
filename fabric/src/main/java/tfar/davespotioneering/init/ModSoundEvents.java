package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tfar.davespotioneering.DavesPotioneeringFabric;

import java.lang.reflect.Field;
import java.util.Locale;

public class ModSoundEvents {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");
    public static final SoundEvent GAUNTLET_TURNING_ON = createSound("gauntlet_turning_on");
    public static final SoundEvent GAUNTLET_TURNING_OFF = createSound("gauntlet_turning_off");
    public static final SoundEvent GAUNTLET_EQUIP = createSound("gauntlet_equip");

    public static final SoundEvent BUBBLING_WATER_CAULDRON = createSound("bubbling_water_cauldron");
    public static final SoundEvent UMBRELLA_OPEN = createSound("umbrella_open");
    public static final SoundEvent UMBRELLA_CLOSE = createSound("umbrella_close");



    private static SoundEvent createSound(String name) {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation(DavesPotioneeringFabric.MODID, name));
    }

    public static void register() {
        for (Field field : ModSoundEvents.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof SoundEvent soundEvent) {
                    Registry.register(BuiltInRegistries.SOUND_EVENT,new ResourceLocation(DavesPotioneeringFabric.MODID,field.getName().toLowerCase(Locale.ROOT)),soundEvent);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
