package tfar.davespotioneering.datagen.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput p_126511_, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_,lookupProvider, DavesPotioneering.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.CAULDRONS).add(ModBlocks.REINFORCED_CAULDRON, ModBlocks.REINFORCED_WATER_CAULDRON);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.COMPOUND_BREWING_STAND, ModBlocks.POTION_INJECTOR);
        tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.MAGIC_LECTERN);
    }
}
