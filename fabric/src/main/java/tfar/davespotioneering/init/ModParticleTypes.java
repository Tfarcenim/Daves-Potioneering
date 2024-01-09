package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import tfar.davespotioneering.DavesPotioneeringFabric;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModParticleTypes {
    private static List<ParticleType<?>> MOD_PARTICLE_TYPES;

    public static final SimpleParticleType FAST_DRIPPING_WATER = new SimpleParticleType(false){};
    public static final SimpleParticleType FAST_FALLING_WATER = new SimpleParticleType(false){};
    public static final SimpleParticleType TINTED_SPLASH = new SimpleParticleType(false){};

    public static void register() {
        for (Field field : ModParticleTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof ParticleType) {
                    Registry.register(BuiltInRegistries.PARTICLE_TYPE,new ResourceLocation(DavesPotioneeringFabric.MODID,field.getName().toLowerCase(Locale.ROOT)),(ParticleType<?>)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
