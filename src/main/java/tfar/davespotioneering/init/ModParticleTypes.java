package tfar.davespotioneering.init;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModParticleTypes {
    private static List<ParticleType<?>> MOD_PARTICLE_TYPES;

    public static final SimpleParticleType FAST_DRIPPING_WATER = new SimpleParticleType(false);
    public static final SimpleParticleType FAST_FALLING_WATER = new SimpleParticleType(false);
    public static final SimpleParticleType TINTED_SPLASH = new SimpleParticleType(false);

    public static void register(RegistryEvent.Register<ParticleType<?>> e) {
        for (Field field : ModParticleTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof ParticleType) {
                    e.getRegistry().register(((ParticleType<?>) o).setRegistryName(field.getName().toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
