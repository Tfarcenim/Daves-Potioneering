package tfar.davespotioneering.init;

import net.minecraft.world.inventory.MenuType;
import tfar.davespotioneering.menu.AdvancedBrewingStandMenu;
import tfar.davespotioneering.menu.PotionInjectorMenu;

public class ModMenuTypes {
    public static final MenuType<AdvancedBrewingStandMenu> ADVANCED_BREWING_STAND = new MenuType<>(AdvancedBrewingStandMenu::new);
    public static final MenuType<PotionInjectorMenu> ALCHEMICAL_GAUNTLET = new MenuType<>(PotionInjectorMenu::new);
}
