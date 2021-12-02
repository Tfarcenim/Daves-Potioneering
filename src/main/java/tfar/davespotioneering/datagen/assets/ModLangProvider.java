package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.GauntletWorkstationBlock;
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

        add("davespotioneering.gui.moveGauntletHUD", "Use your mouse to drag the Gauntlet HUD wherever you would like or use one of these default positions.");
        add("davespotioneering.gui.moveGauntletHUD.preset1", "Left Top");
        add("davespotioneering.gui.moveGauntletHUD.preset2", "Right Top");
        add("davespotioneering.gui.moveGauntletHUD.preset3", "Left Bottom");
        add("davespotioneering.gui.moveGauntletHUD.preset4", "Right Bottom");
        add("davespotioneering.gui.moveGauntletHUD.preset5", "Above Hotbar");
        add("davespotioneering.tooltip.gauntlet", "%s %s");
        add("davespotioneering.tooltip.gauntlet.withDuration", "%s %s (%s)");
    }

    public void addPotions() {
        add(ModPotions.MILK.getNamePrefixed(Items.POTION.getTranslationKey() + ".effect."),"Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.SPLASH_POTION.getTranslationKey() + ".effect."),"Splash Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.LINGERING_POTION.getTranslationKey() + ".effect."),"Lingering Milk Bottle");

    }
}
