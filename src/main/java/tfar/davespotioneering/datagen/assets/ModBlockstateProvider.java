package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.AdvancedBrewingStandBlock;
import tfar.davespotioneering.init.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DavesPotioneering.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        brewingStand();
    }

    protected void brewingStand() {
        getMultipartBuilder(ModBlocks.ADVANCED_BREWING_STAND)
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand"))).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_bottle0"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[0], true).end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_bottle1"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[1], true).end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_bottle2"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[2], true).end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_empty0"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[0], false).end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_empty1"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[1], false).end()
                .part().modelFile(models().getExistingFile(modLoc("block/advanced_brewing_stand_empty2"))).addModel().condition(AdvancedBrewingStandBlock.HAS_BOTTLE[2], false).end()
        ;
    }
}
