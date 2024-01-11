package tfar.davespotioneering.init;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

public class ModMenuTypes {
    public static final MenuType<CAdvancedBrewingStandMenu> ADVANCED_BREWING_STAND = new MenuType<>(CAdvancedBrewingStandMenu::new, FeatureFlagSet.of(FeatureFlags.VANILLA));
    public static final MenuType<CPotionInjectorMenu> ALCHEMICAL_GAUNTLET = new MenuType<>(CPotionInjectorMenu::new, FeatureFlagSet.of(FeatureFlags.VANILLA));
}
