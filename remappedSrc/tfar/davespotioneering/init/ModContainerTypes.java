package tfar.davespotioneering.init;

import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.inventory.MenuType;

public class ModContainerTypes {
    private static List<MenuType<?>> MOD_CONTAINER_TYPES;

    public static final MenuType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = new MenuType<>(AdvancedBrewingStandContainer::new);
    public static final MenuType<PotionInjectorMenu> ALCHEMICAL_GAUNTLET = new MenuType<>(PotionInjectorMenu::new);

    public static void register() {
        for (Field field : ModContainerTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof MenuType) {
                    Registry.register(Registry.SCREEN_HANDLER,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(MenuType<?>)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
