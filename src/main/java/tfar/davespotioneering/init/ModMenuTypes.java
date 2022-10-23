package tfar.davespotioneering.init;

import net.minecraft.world.inventory.MenuType;
import tfar.davespotioneering.menu.AdvancedBrewingStandContainer;
import tfar.davespotioneering.menu.PotionInjectorMenu;

public class ModMenuTypes {
    public static final MenuType<AdvancedBrewingStandContainer> ADVANCED_BREWING_STAND = new MenuType<>(AdvancedBrewingStandContainer::new);
    public static final MenuType<PotionInjectorMenu> ALCHEMICAL_GAUNTLET = new MenuType<>(PotionInjectorMenu::new);
}
