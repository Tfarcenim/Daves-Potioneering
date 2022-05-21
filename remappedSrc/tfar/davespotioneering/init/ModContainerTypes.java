package tfar.davespotioneering.init;

import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModContainerTypes {
    private static List<ScreenHandlerType<?>> MOD_CONTAINER_TYPES;

    public static final ScreenHandlerType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = new ScreenHandlerType<>(AdvancedBrewingStandContainer::new);
    public static final ScreenHandlerType<PotionInjectorMenu> ALCHEMICAL_GAUNTLET = new ScreenHandlerType<>(PotionInjectorMenu::new);

    public static void register() {
        for (Field field : ModContainerTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof ScreenHandlerType) {
                    Registry.register(Registry.SCREEN_HANDLER,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(ScreenHandlerType<?>)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
