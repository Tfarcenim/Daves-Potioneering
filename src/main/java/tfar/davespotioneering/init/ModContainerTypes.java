package tfar.davespotioneering.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModContainerTypes {
    private static List<MenuType<?>> MOD_CONTAINER_TYPES;

    public static final MenuType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = new MenuType<>(AdvancedBrewingStandContainer::new);
    public static final MenuType<PotionInjectorMenu> ALCHEMICAL_GAUNTLET = new MenuType<>(PotionInjectorMenu::new);

    public static void register(RegistryEvent.Register<MenuType<?>> e) {
        for (Field field : ModContainerTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof MenuType) {
                    e.getRegistry().register(((MenuType<?>) o).setRegistryName(field.getName().toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
