package tfar.davespotioneering.datagen.assets;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.AdvancedBrewingStandBlock;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;
import tfar.davespotioneering.init.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, DavesPotioneering.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        brewingStand();


        simpleBlock(ModBlocks.REINFORCED_CAULDRON,models().getExistingFile(modLoc("block/reinforced_cauldron_level0")));

        getVariantBuilder(ModBlocks.REINFORCED_WATER_CAULDRON).forAllStatesExcept(state -> {
            ModelFile modelFile = models().getExistingFile(modLoc("block/reinforced_cauldron_level" + state.getValue(LayeredCauldronBlock.LEVEL)));
            return ConfiguredModel.builder().modelFile(modelFile).build();
        },LayeredReinforcedCauldronBlock.DRAGONS_BREATH);

        getVariantBuilder(ModBlocks.MAGIC_LECTERN).forAllStatesExcept(state -> {
            Direction facing = state.getValue(LecternBlock.FACING);
            ModelFile modelFile = models().getExistingFile(modLoc("block/magic_lectern"));
            return ConfiguredModel.builder().modelFile(modelFile).rotationY((facing.get2DDataValue() + 3) % 4 * 90).build();
        }, LecternBlock.HAS_BOOK, LecternBlock.POWERED);
        blockstateFromExistingModel(ModBlocks.POTION_INJECTOR);
    }

    protected void blockstateFromExistingModel(Block block) {
        ModelFile modelFile = models().getExistingFile(new ResourceLocation(DavesPotioneering.MODID, "block/" + block.getRegistryName().getPath()));
        getVariantBuilder(block).forAllStates(state -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(modelFile);
            if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
                switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
                    case EAST -> builder.rotationY(90);
                    case SOUTH -> builder.rotationY(180);
                    case WEST -> builder.rotationY(270);
                    case NORTH -> builder.rotationY(0);
                }
            }
            return builder.build();
        });
    }

    protected void brewingStand() {
        getMultipartBuilder(ModBlocks.COMPOUND_BREWING_STAND)
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
