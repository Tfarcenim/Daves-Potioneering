package tfar.davespotioneering.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.davespotioneering.block.*;

import java.util.List;

public class ModBlocks {

    private static List<Block> MOD_BLOCKS;

    public static final Block COMPOUND_BREWING_STAND = new CAdvancedBrewingStandBlock(BlockBehaviour.Properties.of()
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());

    public static final Block REINFORCED_CAULDRON = new CReinforcedCauldronBlock(BlockBehaviour.Properties.of()
            .requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion(),ModCauldronInteractions.EMPTY);

    public static final Block REINFORCED_WATER_CAULDRON = new CLayeredReinforcedCauldronBlock(BlockBehaviour.Properties.copy(REINFORCED_CAULDRON));

    public static final Block MAGIC_LECTERN = new CMagicLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN));

    public static final Block POTION_INJECTOR = new CPotionInjectorBlock(BlockBehaviour.Properties.copy(Blocks.FLETCHING_TABLE).noOcclusion());

}
