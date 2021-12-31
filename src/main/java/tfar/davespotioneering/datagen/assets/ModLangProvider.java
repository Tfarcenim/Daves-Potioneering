package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.GauntletWorkstationBlock;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.client.GauntletHUDMovementGui;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModEffects;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, DavesPotioneering.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addEffect(() -> ModEffects.MILK,"Milk");
        addPotions();
        add("container.davespotioneering.advanced_brewing","Advanced Brewing Stand");
        add(GauntletWorkstationBlock.TRANS_KEY,"Potion Injector");
        addBlock(() -> ModBlocks.ADVANCED_BREWING_STAND,"Advanced Brewing Stand");
        addBlock(() -> ModBlocks.REINFORCED_CAULDRON,"Reinforced Cauldron");
        addItem(() -> ModItems.ALCHEMICAL_GAUNTLET,"Alchemical Gauntlet");
        addItem(() -> ModItems.UMBRELLA,"Umbrella");
        addItem(() -> ModItems.GENTLEMAN_UMBRELLA,"Gentleman Umbrella");
        addItem(() -> ModItems.CLEAR_UMBRELLA,"Clear Umbrella");
        addItem(() -> ModItems.RUDIMENTARY_GAUNTLET,getNameFromItem(ModItems.RUDIMENTARY_GAUNTLET));
        addItem(() -> ModItems.NETHERITE_GAUNTLET,getNameFromItem(ModItems.NETHERITE_GAUNTLET));

        add("davespotioneering.gui.moveGauntletHUD", "Use your mouse to drag the Gauntlet HUD wherever you would like or use one of these default positions.");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.TOP_LEFT.ordinal(), "Left Top");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.TOP_RIGHT.ordinal(), "Right Top");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.BTM_LEFT.ordinal(), "Left Bottom");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.BTM_RIGHT.ordinal(), "Right Bottom");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.ABOVE_HOTBAR.ordinal(), "Above Hotbar");
        add("davespotioneering.tooltip.gauntlet", "%s %s");
        add("davespotioneering.tooltip.gauntlet.withDuration", "%s %s (%s)");
        addGroup(ModItems.tab,"Dave's Potioneering");
    }

    protected void addGroup(ItemGroup group,String name) {
        add(group.getGroupName().getString(),name);
    }

    public void addPotions() {
        add(ModPotions.MILK.getNamePrefixed(Items.POTION.getTranslationKey() + ".effect."),"Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.SPLASH_POTION.getTranslationKey() + ".effect."),"Splash Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.LINGERING_POTION.getTranslationKey() + ".effect."),"Lingering Milk Bottle");
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getTranslationKey().split("\\.")[2].replace("_", " "));
    }
}
