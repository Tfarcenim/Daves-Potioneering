package tfar.davespotioneering.datagen.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModItems;

import javax.annotation.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, DavesPotioneering.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModItems.BLACKLISTED).add(Items.POTION, Items.SPLASH_POTION,Items.LINGERING_POTION);
        tag(ModItems.WHITELISTED);
    }
}
