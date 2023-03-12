package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.DavesPotioneering;

import java.lang.reflect.Field;
import java.util.Locale;

public class ModSoundEvents {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");
    public static final SoundEvent GAUNTLET_TURNING_ON = createSound("gauntlet_turning_on");
    public static final SoundEvent GAUNTLET_TURNING_OFF = createSound("gauntlet_turning_off");
    public static final SoundEvent BUBBLING_WATER_CAULDRON = createSound("bubbling_water_cauldron");
    public static final SoundEvent UMBRELLA_OPEN = createSound("umbrella_open");
    public static final SoundEvent UMBRELLA_CLOSE = createSound("umbrella_close");

    public static final SoundEvent GAUNTLET_EQUIP = createSound("gauntlet_equip");


    private static SoundEvent createSound(String name) {
        return new SoundEvent(new ResourceLocation(DavesPotioneering.MODID, name));
    }

    public static void register(RegistryEvent.Register<SoundEvent> e) {
        for (Field field : ModSoundEvents.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof SoundEvent) {
                    e.getRegistry().register(((SoundEvent) o).setRegistryName(field.getName().toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
