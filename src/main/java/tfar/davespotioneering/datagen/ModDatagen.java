package tfar.davespotioneering.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import tfar.davespotioneering.datagen.assets.ModLangProvider;

public class ModDatagen {

    public static void start(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();

        if (e.includeClient()) {
            generator.addProvider(new ModLangProvider(generator));
        }
    }
}
