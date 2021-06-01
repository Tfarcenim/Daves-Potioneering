package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModEffects;
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
        addBlock(() -> ModBlocks.ADVANCED_BREWING_STAND,"Advanced Brewing Stand");
    }

    public void addPotions() {
        add(ModPotions.MILK.getNamePrefixed(Items.POTION.getTranslationKey() + ".effect."),"Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.SPLASH_POTION.getTranslationKey() + ".effect."),"Splash Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.LINGERING_POTION.getTranslationKey() + ".effect."),"Lingering Milk Bottle");

    }
}
