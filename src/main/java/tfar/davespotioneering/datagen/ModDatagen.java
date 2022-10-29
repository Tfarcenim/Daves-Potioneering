package tfar.davespotioneering.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.davespotioneering.datagen.assets.ModBlockstateProvider;
import tfar.davespotioneering.datagen.assets.ModItemModelProvider;
import tfar.davespotioneering.datagen.assets.ModLangProvider;
import tfar.davespotioneering.datagen.data.ModBlockTagsProvider;
import tfar.davespotioneering.datagen.data.ModLootTableProvider;
import tfar.davespotioneering.datagen.data.ModRecipeProvider;

public class ModDatagen {

    public static void start(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();

        if (e.includeClient()) {
            generator.addProvider(true,new ModLangProvider(generator));
            generator.addProvider(true,new ModBlockstateProvider(generator,helper));
            generator.addProvider(true,new ModItemModelProvider(generator,helper));
        }
        if (e.includeServer()) {
            generator.addProvider(true,new ModLootTableProvider(generator));
            generator.addProvider(true,new ModRecipeProvider(generator));
            generator.addProvider(true,new ModBlockTagsProvider(generator,helper));
        }
    }
}
