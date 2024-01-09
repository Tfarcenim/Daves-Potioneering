package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModContainerTypes {
    private static List<MenuType<?>> MOD_CONTAINER_TYPES;

    public static final MenuType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = make(AdvancedBrewingStandContainer::new);
    public static final MenuType<CPotionInjectorMenu> ALCHEMICAL_GAUNTLET = make(CPotionInjectorMenu::new);

    static <T extends AbstractContainerMenu> MenuType<T> make(MenuType.MenuSupplier<T> supplier) {
        return new MenuType<>(supplier, FeatureFlagSet.of(FeatureFlags.VANILLA));
    }

    public static void register() {
        for (Field field : ModContainerTypes.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof MenuType) {
                    Registry.register(BuiltInRegistries.MENU,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(MenuType<?>)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }
}
