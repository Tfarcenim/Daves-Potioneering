package tfar.davespotioneering.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModContainerTypes {
    private static List<ContainerType<?>> MOD_CONTAINER_TYPES;

    public static final ContainerType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = new ContainerType<>(AdvancedBrewingStandContainer::new);

    public static void register(RegistryEvent.Register<ContainerType<?>> e) {
        for (Field field : ModContainerTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof ContainerType) {
                    e.getRegistry().register(((ContainerType<?>) o).setRegistryName(field.getName().toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
