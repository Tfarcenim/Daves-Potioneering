package tfar.davespotioneering.datagen.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator p_126511_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_, DavesPotioneering.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.CAULDRONS).add(ModBlocks.REINFORCED_CAULDRON,ModBlocks.REINFORCED_WATER_CAULDRON);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.COMPOUND_BREWING_STAND,ModBlocks.POTION_INJECTOR);
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.MAGIC_LECTERN);
    }
}
