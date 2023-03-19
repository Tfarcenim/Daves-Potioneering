package tfar.davespotioneering.init;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;

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
        return new SoundEvent(new Identifier(DavesPotioneering.MODID, name));
    }

    public static void register() {
        for (Field field : ModSoundEvents.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof SoundEvent soundEvent) {
                    Registry.register(Registry.SOUND_EVENT,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),soundEvent);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
