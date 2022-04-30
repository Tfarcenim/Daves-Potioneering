package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tfar.davespotioneering.DavesPotioneering;

import java.lang.reflect.Field;
import java.util.Locale;

public class ModSoundEvents {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");
    public static final SoundEvent GAUNTLET_TURNING_ON = createSound("gauntlet_turning_on");
    public static final SoundEvent GAUNTLET_TURNING_OFF = createSound("gauntlet_turning_off");
    public static final SoundEvent GAUNTLET_EQUIP = createSound("gauntlet_equip");


    private static SoundEvent createSound(String name) {
        return new SoundEvent(new ResourceLocation(DavesPotioneering.MODID, name));
    }

    public static void register() {
        for (Field field : ModSoundEvents.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof SoundEvent) {
                    Registry.register(Registry.SOUND_EVENT,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(SoundEvent)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
