package tfar.davespotioneering.init;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.DavesPotioneering;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModParticleTypes {
    private static List<ParticleType<?>> MOD_PARTICLE_TYPES;

    public static final DefaultParticleType FAST_DRIPPING_WATER = new DefaultParticleType(false){};
    public static final DefaultParticleType FAST_FALLING_WATER = new DefaultParticleType(false){};
    public static final DefaultParticleType TINTED_SPLASH = new DefaultParticleType(false){};

    public static void register() {
        for (Field field : ModParticleTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof ParticleType) {
                    Registry.register(Registry.PARTICLE_TYPE,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(ParticleType<?>)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
