package tfar.davespotioneering.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.DavesPotioneering;

import java.lang.reflect.Field;
import java.util.Locale;

public class ModSounds {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");

    private static SoundEvent createSound(String name) {
        return new SoundEvent(new ResourceLocation(DavesPotioneering.MODID, name));
    }

    public static void register(RegistryEvent.Register<SoundEvent> e) {
        for (Field field : ModItems.class.getFields()) {
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
