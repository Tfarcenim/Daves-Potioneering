package tfar.davespotioneering.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.util.Locale;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> void superRegister(Class<?> clazz, Registry<T> registry, Class<? extends T> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    Registry.register(registry,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(T)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
