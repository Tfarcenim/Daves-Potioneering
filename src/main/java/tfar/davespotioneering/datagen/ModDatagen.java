package tfar.davespotioneering.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import tfar.davespotioneering.datagen.assets.ModBlockstateProvider;
import tfar.davespotioneering.datagen.assets.ModItemModelProvider;
import tfar.davespotioneering.datagen.assets.ModLangProvider;
import tfar.davespotioneering.datagen.data.ModLootTableProvider;
import tfar.davespotioneering.datagen.data.ModRecipeProvider;

public class ModDatagen {

    public static void start(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();

        if (e.includeClient()) {
            generator.addProvider(new ModLangProvider(generator));
            generator.addProvider(new ModBlockstateProvider(generator,helper));
            generator.addProvider(new ModItemModelProvider(generator,helper));
        }
        if (e.includeServer()) {
            generator.addProvider(new ModLootTableProvider(generator));
            generator.addProvider(new ModRecipeProvider(generator));
        }
    }
}
